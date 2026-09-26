package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.command.CreateTransactionCommand;
import com.alibou.finance.account.domain.agregate.Transaction;

public interface CreateTransactionUseCase {
    Transaction execute(CreateTransactionCommand command);
}
