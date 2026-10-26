package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.result.AccountLifeCycleResult;

public interface ReactivateAccountUseCase {
    AccountLifeCycleResult execute(AccountLifeCycleCommand input);
}
