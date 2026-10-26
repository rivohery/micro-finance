package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.command.DepositCommand;
import com.alibou.finance.account.application.port.result.TransactionResult;


public interface DepositUseCase {
    TransactionResult execute(DepositCommand input);
}
