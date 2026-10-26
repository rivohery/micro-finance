package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.command.CreateAccountCommand;
import com.alibou.finance.account.application.port.usecase.CreateNewAccountUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.linker.AccountTypeLinkerPort;
import com.alibou.finance.account.domain.out.linker.CurrencyLinkerPort;
import com.alibou.finance.account.domain.out.linker.CustomerLinkerPort;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.service.AccountNumberGenerator;
import com.alibou.finance.account.domain.out.linker.CurrencyExchangePort;
import com.alibou.finance.accountType.AccountTypeInfo;
import com.alibou.finance.currency.CurrencyInfo;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.account.domain.vo.account.OverdraftLimit;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.domain.exception.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CreateNewAccountServiceApplication implements CreateNewAccountUseCase {
    private static final String DEFAULT_AGENCE_NUMBER = "001";//Pour ne pas ajouter un module Agence
    private static final int ACCOUNT_NUMBER_NUMERIC_LENGTH = 10;
    private static final String CURRENCY_REFERENCE_CODE = "MGA";
    private final AccountTypeLinkerPort accountTypeLinkerPort;
    private final CurrencyLinkerPort currencyLinkerPort;
    private final CustomerLinkerPort customerLinkerPort;
    private final AccountNumberGenerator accountNumberGenerator;
    private final CurrencyExchangePort currencyExchangePort;
    private final AccountRepository accountRepository;

    @Override
    public Account execute(CreateAccountCommand createAccountCommand) {
        validateCustomerActive(createAccountCommand.customerId());

        AccountTypeInfo accountType = accountTypeLinkerPort.getAccountTypeByCode(createAccountCommand.accountTypeCode());
        CurrencyInfo currency = currencyLinkerPort.getCurrencyByCode(createAccountCommand.currencyCode());

        //On calcule le découvert à partir du cel définie dans le type du compte (minimumBalance en MGA) et le taux d'échange au moment de la création du compte
        BigDecimal exchangeRate = currencyExchangePort.getExchangeRate(CURRENCY_REFERENCE_CODE, currency.currencyCode().value());
        OverdraftLimit overdraftLimit = OverdraftLimit.calculate(accountType.minimumBalance().value(), exchangeRate);

        String accountNumberRaw = accountNumberGenerator.generateUniqueAccountNumber(
                DEFAULT_AGENCE_NUMBER, accountType.accountTypeCode().value(), ACCOUNT_NUMBER_NUMERIC_LENGTH
        );
        AccountNumber accountNumber = new AccountNumber(accountNumberRaw);
        Account newAccount = Account.initializeNewAccount(accountNumber, accountType, currency, overdraftLimit, createAccountCommand.customerId());
        return accountRepository.save(newAccount);
    }

    private void validateCustomerActive(CustomerId customerId){
        var customerIsActive = customerLinkerPort.verifyIfCustomerIsActive(customerId);
        if(!customerIsActive){
            throw new OperationNotPermittedException("Ce client n'est pas activé");
        }
    }
}
