package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.command.WithdrawCommand;
import com.alibou.finance.account.application.port.result.TransactionResult;
import com.alibou.finance.account.application.port.usecase.CreateTransactionUseCase;
import com.alibou.finance.account.application.port.usecase.WithdrawUseCase;
import com.alibou.finance.account.application.utils.TransactionOperationExecutor;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.vo.transaction.FinalAmount;
import com.alibou.finance.account.domain.vo.transaction.SoldBeforeTransaction;
import com.alibou.finance.account.application.port.command.CreateTransactionCommand;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.agregate.TransactionTypeEnum;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class WithdrawServiceApplication implements WithdrawUseCase {
    private final TransactionOperationExecutor transactionOperationExecutor;
    private final CreateTransactionUseCase createTransactionUseCase;

    @Override
    public TransactionResult execute(WithdrawCommand input) {
        Account account = transactionOperationExecutor.getAccountByAccountNumber(input.accountNumber());

        SoldBeforeTransaction soldBeforeTransaction = SoldBeforeTransaction.buildFrom(account.getBalance().value());

        BigDecimal exchangeRate = transactionOperationExecutor.getExchangeRate(
                input.transactionCurrencyCode().value(), account.getCurrencyCode().value()
        );
        FinalAmount finalAmount = transactionOperationExecutor.convertToFinalAmount(input.originalAmount().value(), exchangeRate);

        account.makeWithdraw(finalAmount);
        account = transactionOperationExecutor.updateAndSaveAccount(account);

        CreateTransactionCommand transactionCommand = transactionOperationExecutor.prepareTransactionCommand(
                account,
                input.transactionCurrencyCode(),
                TransactionTypeEnum.WITHDRAWAL,
                exchangeRate,
                input.originalAmount(),
                input.username(),
                soldBeforeTransaction,
                input.description()
        );
        Transaction withdraw = createTransactionUseCase.execute(transactionCommand);

        return TransactionResult.builder()
                .account(account)
                .transaction(withdraw)
                .build();
    }
}
