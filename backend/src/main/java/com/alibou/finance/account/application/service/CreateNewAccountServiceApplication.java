package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.usecase.CreateNewAccountUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.service.AccountNumberGenerator;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.domain.vo.OverdraftLimit;
import com.alibou.finance.accountType.application.port.AccountTypeUseCase;
import com.alibou.finance.currency.application.port.CurrencyUseCase;
import com.alibou.finance.customer.application.port.CustomerLifeCycleUseCase;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CreateNewAccountServiceApplication implements CreateNewAccountUseCase {
    private static final String DEFAULT_AGENCE_NUMBER = "001";//Pour ne pas ajouter un module Agence
    private static final int ACCOUNT_NUMBER_NUMERIC_LENGTH = 10;
    private static final String CURRENCY_REFERENCE_CODE = "MGA";
    private final CustomerLifeCycleUseCase customerLifeCycleUseCase;
    private final AccountTypeUseCase accountTypeUseCase;
    private final CurrencyUseCase currencyUseCase;
    private final AccountNumberGenerator accountNumberGenerator;
    private final CurrencyExchangePort currencyExchangePort;
    private final AccountRepository accountRepository;

    @Override
    public Account execute(Account account) {
        validateCustomerActive(account.getCustomerId());

        var accountType = accountTypeUseCase.findByCode(account.getAccountType().getCode().value());
        var currency = currencyUseCase.findByCode(account.getCurrency().getCode());

        //On calcule le découvert à partir du cel définie dans le type du compte (minimumBalance en MGA) et le taux d'échange au moment de la création du compte
        BigDecimal exchangeRate = currencyExchangePort.getExchangeRate(CURRENCY_REFERENCE_CODE, currency.getCode().value());//Ex MGA => EUR
        OverdraftLimit overdraftLimit = OverdraftLimit.calculate(accountType.getMinimumBalance().value(), exchangeRate);

        String accountNumberValue = accountNumberGenerator.generateUniqueAccountNumber(
                DEFAULT_AGENCE_NUMBER, accountType.getCode().value(), ACCOUNT_NUMBER_NUMERIC_LENGTH
        );
        AccountNumber accountNumber = AccountNumber.from(accountNumberValue);
        account.initializeNewAccount(accountNumber, accountType, currency, overdraftLimit);
        return accountRepository.save(account);
    }

    private void validateCustomerActive(CustomerId customerId){
        var customerIsActive = customerLifeCycleUseCase.verifyIfCustomerIsActive(customerId);
        if(!customerIsActive){
            throw new OperationNotPermittedException("Client inactive");
        }
    }
}
