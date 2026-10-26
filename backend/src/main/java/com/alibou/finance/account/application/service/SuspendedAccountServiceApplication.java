package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.result.AccountLifeCycleResult;
import com.alibou.finance.account.application.port.usecase.SuspendedAccountUseCase;
import com.alibou.finance.account.application.utils.AccountLifeCycleExecutor;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SuspendedAccountServiceApplication implements SuspendedAccountUseCase {

    private final AccountLifeCycleExecutor accountLifeCycleExecutor;

    @Override
    public AccountLifeCycleResult execute(AccountLifeCycleCommand input) {
        return accountLifeCycleExecutor.executeTransition(
                input,
                Account::suspend
        );
    }
}
