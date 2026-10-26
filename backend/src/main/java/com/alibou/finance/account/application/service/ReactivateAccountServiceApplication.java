package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.result.AccountLifeCycleResult;
import com.alibou.finance.account.application.port.usecase.ReactivateAccountUseCase;
import com.alibou.finance.account.application.utils.AccountLifeCycleExecutor;
import com.alibou.finance.account.domain.agregate.Account;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReactivateAccountServiceApplication implements ReactivateAccountUseCase {

    private final AccountLifeCycleExecutor accountLifeCycleExecutor;

    @Override
    public AccountLifeCycleResult execute(AccountLifeCycleCommand input) {
        return accountLifeCycleExecutor.executeTransition(
                input,
                Account::reactive
        );
    }
}
