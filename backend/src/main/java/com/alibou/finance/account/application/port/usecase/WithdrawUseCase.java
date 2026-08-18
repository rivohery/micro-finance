package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.dto.command.WithdrawCommand;
import com.alibou.finance.account.application.port.dto.output.TransactionResult;

public interface WithdrawUseCase {
    TransactionResult execute(WithdrawCommand input);
}
