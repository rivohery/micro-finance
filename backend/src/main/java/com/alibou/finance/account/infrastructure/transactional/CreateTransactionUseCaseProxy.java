package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.account.application.port.command.CreateTransactionCommand;
import com.alibou.finance.account.application.port.usecase.CreateTransactionUseCase;
import com.alibou.finance.account.domain.agregate.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateTransactionUseCaseProxy {
    private final CreateTransactionUseCase createTransactionUseCase;

    @Transactional
    public Transaction execute(CreateTransactionCommand command) {
        return createTransactionUseCase.execute(command);
    }
}
