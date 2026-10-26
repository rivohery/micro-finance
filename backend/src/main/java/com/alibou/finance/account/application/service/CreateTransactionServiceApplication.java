package com.alibou.finance.account.application.service;

import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.application.port.command.CreateTransactionCommand;
import com.alibou.finance.account.application.port.usecase.CreateTransactionUseCase;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.service.TransactionReferenceGenerator;
import com.alibou.finance.account.domain.vo.transaction.FinalAmount;
import com.alibou.finance.account.domain.vo.transaction.Reference;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateTransactionServiceApplication implements CreateTransactionUseCase {
    private static final int REFERENCE_LENGTH = 6;
    private final TransactionRepository transactionRepository;
    private final TransactionReferenceGenerator transactionReferenceGenerator;

    @Override
    public Transaction execute(CreateTransactionCommand command) {
        String generatedValue = transactionReferenceGenerator.generateReferenceCharacter(REFERENCE_LENGTH);
        Reference reference = Transaction.generateReference(command.transactionType(), generatedValue);
        FinalAmount finalAmount = new FinalAmount(command.originalAmount().value().multiply(command.exchangeRate().value()));
        Transaction newTransaction = Transaction.initializeNewTransaction(
                command.transactionCurrencyCode(),
                command.targetCurrencyCode(),
                command.soldBeforeTransaction(),
                command.transactionType(),
                command.accountId(),
                command.accountNumber(),
                command.description(),
                command.exchangeRate(),
                command.originalAmount(),
                command.operatorName(),
                finalAmount,
                reference
        );
        return transactionRepository.save(newTransaction);
    }
}
