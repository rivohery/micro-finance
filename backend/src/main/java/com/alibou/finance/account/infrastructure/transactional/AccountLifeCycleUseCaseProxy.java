package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.account.application.port.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.result.AccountLifeCycleResult;
import com.alibou.finance.account.application.port.usecase.ActivateAccountUseCase;
import com.alibou.finance.account.application.port.usecase.CloseAccountUseCase;
import com.alibou.finance.account.application.port.usecase.ReactivateAccountUseCase;
import com.alibou.finance.account.application.port.usecase.SuspendedAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AccountLifeCycleUseCaseProxy {


   private final ActivateAccountUseCase activateAccountUseCase;
   private final ReactivateAccountUseCase reactivateAccountUseCase;
   private final SuspendedAccountUseCase suspendedAccountUseCase;
   private final CloseAccountUseCase closeAccountUseCase;

    @Transactional
    public AccountLifeCycleResult activateAccount(AccountLifeCycleCommand input) {
        return activateAccountUseCase.execute(input);
    }

    @Transactional
    public AccountLifeCycleResult reactivateAccount(AccountLifeCycleCommand input) {
        return reactivateAccountUseCase.execute(input);
    }

    @Transactional
    public AccountLifeCycleResult suspendAccount(AccountLifeCycleCommand input) {
        return suspendedAccountUseCase.execute(input);
    }

    @Transactional
    public AccountLifeCycleResult closeAccount(AccountLifeCycleCommand input) {
        return closeAccountUseCase.execute(input);
    }



}
