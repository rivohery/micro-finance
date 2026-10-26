package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.command.TransferCommand;
import com.alibou.finance.account.application.port.result.TransferResult;

public interface TransferUseCase {
    TransferResult execute(TransferCommand input);
}
