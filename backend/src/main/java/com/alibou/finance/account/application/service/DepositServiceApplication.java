package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.command.DepositCommand;
import com.alibou.finance.account.application.port.result.TransactionResult;
import com.alibou.finance.account.application.port.usecase.CreateTransactionUseCase;
import com.alibou.finance.account.application.port.usecase.DepositUseCase;
import com.alibou.finance.account.application.utils.TransactionOperationExecutor;
import com.alibou.finance.account.domain.vo.transaction.FinalAmount;
import com.alibou.finance.account.domain.vo.transaction.SoldBeforeTransaction;
import com.alibou.finance.account.application.port.command.CreateTransactionCommand;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.agregate.TransactionTypeEnum;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class DepositServiceApplication implements DepositUseCase {

    private final TransactionOperationExecutor transactionOperationExecutor;
    private final CreateTransactionUseCase createTransactionUseCase;

    @Override
    public TransactionResult execute(DepositCommand input) {
        var account = transactionOperationExecutor.getAccountByAccountNumber(input.accountNumber());

        /***Pour le calcul de taux d'intérêt***/
        SoldBeforeTransaction soldBeforeTransaction = SoldBeforeTransaction.buildFrom(account.getBalance().value());

        BigDecimal exchangeRate = transactionOperationExecutor.getExchangeRate(input.transactionCurrencyCode().value(), account.getCurrencyCode().value());
        FinalAmount finalAmount = transactionOperationExecutor.convertToFinalAmount(input.originalAmount().value(), exchangeRate);
        account.makeDeposit(finalAmount);
        account = transactionOperationExecutor.updateAndSaveAccount(account);

        CreateTransactionCommand transactionCommand = transactionOperationExecutor.prepareTransactionCommand(
                account,
                input.transactionCurrencyCode(),
                TransactionTypeEnum.DEPOSIT,
                exchangeRate,
                input.originalAmount(),
                input.username(),
                soldBeforeTransaction,
                input.description()
        );
        Transaction deposit = createTransactionUseCase.execute(transactionCommand);
        return TransactionResult.builder()
                .account(account)
                .transaction(deposit)
                .build();
    }

}
