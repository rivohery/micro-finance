package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.dto.input.AccountLifeCycleInput;
import com.alibou.finance.account.domain.agregate.OverdraftLimit;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.currency.application.port.CurrencyUseCase;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.account.application.port.usecase.AccountTypeUseCase;
import com.alibou.finance.account.application.port.usecase.AccountLifeCycleUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.domain.exception.AccountNotFoundException;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.service.AccountNumberGenerator;
import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.customer.application.port.CustomerUseCase;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.log.application.port.input.AccountStatusHistoryInput;
import com.alibou.finance.log.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.domain.vo.*;
import com.alibou.finance.shared.error.domain.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountLifeCycleServiceApplication implements AccountLifeCycleUseCase {
    private static final String DEFAULT_AGENCE_NUMBER = "001";//Pour ne pas ajouter un module Agence
    private static final int ACCOUNT_NUMBER_NUMERIC_LENGTH = 10;
    private static final String CURRENCY_REFERENCE_CODE = "MGA";
    private final AccountRepository accountRepository;
    private final CustomerUseCase customerUseCase;
    private final AccountTypeUseCase accountTypeUseCase;
    private final CurrencyUseCase currencyUseCase;
    private final AccountNumberGenerator accountNumberGenerator;
    private final CurrencyExchangePort currencyExchangePort;
    private final UserUseCase userUseCase;
    private final AccountStatusHistoryUseCase accountStatusHistoryUseCase;

    @Override
    @Transactional
    public Account create(Account account) {
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

    @Override
    @Transactional
    public Map<String, Object> activateAccount(AccountLifeCycleInput input) {
        User user = checkAndVerifyStatusOfEmployee(input.changedBy().value());

        Account account = checkAccountByAccountId(input.accountId());
        OldStatus oldStatus = new OldStatus(account.getAccountStatus().value());
        account.activeAccount();
        account = accountRepository.save(account);
        NewStatus newStatus = new NewStatus(AccountStatusEnum.ACTIVE);

        AccountStatusHistory history =  prepareAndSaveAccountStatusHistory(account, user, oldStatus, newStatus, input.reason());
        return Map.of(
                "accountId", account.getAccountId(),
                "accountHistoryId", history.getAccountStatusHistoryId(),
                "newStatus", account.getAccountStatus().value()
        );
    }

    @Override
    @Transactional
    public Map<String, Object> suspendAccount(AccountLifeCycleInput input) {
        User user = checkAndVerifyStatusOfEmployee(input.changedBy().value());

        Account account = checkAccountByAccountId(input.accountId());
        OldStatus oldStatus = new OldStatus(account.getAccountStatus().value());
        account.suspendAccount();
        account = accountRepository.save(account);
        NewStatus newStatus = new NewStatus(AccountStatusEnum.SUSPENDED);

        AccountStatusHistory history =  prepareAndSaveAccountStatusHistory(account, user, oldStatus, newStatus, input.reason());
        return Map.of(
                "accountId", account.getAccountId(),
                "accountHistoryId", history.getAccountStatusHistoryId(),
                "newStatus", account.getAccountStatus().value()
        );
    }

    @Override
    @Transactional
    public Map<String, Object> closeAccount(AccountLifeCycleInput input) {
        var user = checkAndVerifyStatusOfEmployee(input.changedBy().value());

        var account = checkAccountByAccountId(input.accountId());
        OldStatus oldStatus = new OldStatus(account.getAccountStatus().value());
        account.closeAccount();
        account = accountRepository.save(account);
        NewStatus newStatus = new NewStatus(AccountStatusEnum.CLOSED);

        var accountStatusHistory =  prepareAndSaveAccountStatusHistory(account, user, oldStatus, newStatus, input.reason());
        return Map.of(
                "accountId", account.getAccountId(),
                "accountHistoryId", accountStatusHistory.getAccountStatusHistoryId(),
                "newStatus", account.getAccountStatus().value()
        );
    }

    private void validateCustomerActive(CustomerId customerId){
        var customerIsActive = customerUseCase.verifyIfCustomerIsActive(customerId);
        if(!customerIsActive){
            throw new OperationNotPermittedException("Client inactive");
        }
    }

    private User checkAndVerifyStatusOfEmployee(UUID id){
        User user = userUseCase.findByUserId(UserId.from(id));
        if(!user.isEnable()){
            throw new OperationNotPermittedException("Employé non activé");
        }
        return user;
    }
    private Account checkAccountByAccountId(AccountId accountId){
        return  accountRepository.findById(accountId).orElseThrow(
                ()-> new AccountNotFoundException("Compte non trouvé")
        );
    }

    private AccountStatusHistory prepareAndSaveAccountStatusHistory(
            Account account,
            User user,
            OldStatus oldStatus,
            NewStatus newStatus,
            Reason reason
    ){
        AccountStatusHistoryInput accountStatusHistoryInput =  AccountStatusHistoryInput
                .builder()
                .accountId(account.getAccountId())
                .doingBy(DoingBy.from(user.getUsername().value()))
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .reason(reason)
                .build();
        return accountStatusHistoryUseCase.save(accountStatusHistoryInput);
    }
}
