package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.dto.command.TransferCommand;
import com.alibou.finance.account.application.port.dto.output.TransferResult;

public interface TransferUseCase {
    TransferResult execute(TransferCommand input);
}
