package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.account.application.port.dto.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.dto.output.AccountLifeCycleResult;
import com.alibou.finance.account.application.port.usecase.AccountLifeCycleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AccountLifeCycleUseCaseProxy {

    private final AccountLifeCycleUseCase accountLifeCycleUseCase;

    @Transactional
    public AccountLifeCycleResult activateAccount(AccountLifeCycleCommand input) {
        return accountLifeCycleUseCase.activateAccount(input);
    }

    @Transactional
    public AccountLifeCycleResult suspendAccount(AccountLifeCycleCommand input) {
        return accountLifeCycleUseCase.suspendAccount(input);
    }

    @Transactional
    public AccountLifeCycleResult closeAccount(AccountLifeCycleCommand input) {
        return accountLifeCycleUseCase.closeAccount(input);
    }

}
