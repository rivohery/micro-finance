package com.alibou.finance.history.application.service;

import com.alibou.finance.history.application.port.command.AccountTypeInterestRateCommand;
import com.alibou.finance.history.application.port.usecase.AccountTypeInterestRateUseCase;
import com.alibou.finance.history.domain.agregate.AccountTypeInterestRate;
import com.alibou.finance.history.domain.out.repository.AccountTypeInterestRateRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountTypeInterestRateServiceApplication implements AccountTypeInterestRateUseCase {

    private final AccountTypeInterestRateRepository accountTypeInterestRateRepository;

    @Override
    public AccountTypeInterestRate create(AccountTypeInterestRateCommand accountTypeInterestRateCommand) {
        AccountTypeInterestRate newAccountTypeInterestRate = AccountTypeInterestRate.prepare(accountTypeInterestRateCommand);
        return accountTypeInterestRateRepository.save(newAccountTypeInterestRate);
    }
}
