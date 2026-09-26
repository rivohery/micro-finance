package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.command.WithdrawCommand;
import com.alibou.finance.account.application.port.result.TransactionResult;

public interface WithdrawUseCase {
    TransactionResult execute(WithdrawCommand input);
}
