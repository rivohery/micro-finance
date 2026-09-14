package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.dto.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.dto.output.AccountLifeCycleResult;


public interface AccountLifeCycleUseCase {

    AccountLifeCycleResult activateAccount(AccountLifeCycleCommand input);
    AccountLifeCycleResult suspendAccount(AccountLifeCycleCommand input);
    AccountLifeCycleResult closeAccount(AccountLifeCycleCommand input);

}
