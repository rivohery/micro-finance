package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.dto.command.DepositCommand;
import com.alibou.finance.account.application.port.dto.output.TransactionResult;


public interface DepositUseCase {
    TransactionResult execute(DepositCommand input);
}
