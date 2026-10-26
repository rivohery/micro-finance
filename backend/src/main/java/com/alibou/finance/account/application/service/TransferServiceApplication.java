package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.command.TransferCommand;
import com.alibou.finance.account.application.port.result.TransactionResult;
import com.alibou.finance.account.application.port.result.TransferResult;
import com.alibou.finance.account.application.port.usecase.CreateTransactionUseCase;
import com.alibou.finance.account.application.port.usecase.TransferUseCase;
import com.alibou.finance.account.application.utils.TransactionOperationExecutor;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.linker.CustomerLinkerPort;
import com.alibou.finance.account.domain.vo.transaction.FinalAmount;
import com.alibou.finance.account.domain.vo.transaction.SoldBeforeTransaction;
import com.alibou.finance.account.application.port.command.CreateTransactionCommand;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationExecutor;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationInfo;
import com.alibou.finance.account.domain.vo.transaction.TransactionCurrencyCode;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.shared.domain.exception.OperationNotPermittedException;
import com.alibou.finance.shared.domain.vo.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class TransferServiceApplication implements TransferUseCase {
    private final TransactionOperationExecutor transactionOperationExecutor;
    private final CustomerLinkerPort customerLinkerPort;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final TransfertConfirmationExecutor transfertConfirmationPort;

    @Override
    public TransferResult execute(TransferCommand input) {
        TransactionResult withdrawResult = processWithdrawOfTransfert(input);
        Account sourceAccount = withdrawResult.account();
        Transaction withdrawTransaction = withdrawResult.transaction();

        TransactionCurrencyCode transactionCurrencyCode = new TransactionCurrencyCode(sourceAccount.getCurrencyCode().value());

        TransactionResult depositResult = processDepositOfTransfert(input, transactionCurrencyCode);
        Account targetAccount = depositResult.account();
        Transaction depositTransaction = depositResult.transaction();

        //On envoie une confirmation de transfert chez client
        TransfertConfirmationInfo transfertConfirmation = TransfertConfirmationInfo.initializeNewInfo(
                sourceAccount.getAccountNumber().value(),
                targetAccount.getAccountNumber().value(),
                sourceAccount.getCurrencyCode().value(),
                targetAccount.getCurrencyCode().value(),
                depositTransaction.getExchangeRate().value(),
                depositTransaction.getFinalAmount().value(),
                withdrawTransaction.getOriginalAmount().value(),
                input.userInfo()
        );

        transfertConfirmationPort.sendTransactionConfirmation(transfertConfirmation);

        return TransferResult.builder()
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .depositTransaction(depositTransaction)
                .withdrawTransaction(withdrawTransaction)
                .build();
    }

    private TransactionResult processWithdrawOfTransfert(TransferCommand cmd){
        Account sourceAccount = transactionOperationExecutor.getAccountByAccountNumber(cmd.sourceAccountNumber());

        SoldBeforeTransaction soldBeforeTransaction = new SoldBeforeTransaction(sourceAccount.getBalance().value());

        //On vérifie si le compte source appartient bien à l'utilisateur connecté
        verifySourceAccountProperty(cmd.userInfo().userId(), sourceAccount);

        // => exchangeRate = 1(la monnaie de la transaction est celle de la monnaie source)
        BigDecimal exchangeRate = BigDecimal.ONE;
        // => finalAmount : identique au montant du transfert

        FinalAmount finalAmount = new FinalAmount(cmd.originalAmount().value());
        sourceAccount.makeWithdraw(finalAmount);
        sourceAccount = transactionOperationExecutor.updateAndSaveAccount(sourceAccount);

        TransactionCurrencyCode transactionCurrencyCode = new TransactionCurrencyCode(
                sourceAccount.getCurrencyCode().value()
        );
        CreateTransactionCommand transactionCommand = transactionOperationExecutor.prepareTransactionCommand(
                sourceAccount,
                transactionCurrencyCode,
                TransactionTypeEnum.WITHDRAWAL,
                exchangeRate,
                cmd.originalAmount(),
                cmd.userInfo().username(),
                soldBeforeTransaction,
                cmd.description()
        );
        Transaction withdraw = createTransactionUseCase.execute(transactionCommand);

        return TransactionResult.builder()
                .account(sourceAccount)
                .transaction(withdraw)
                .build();
    }

    private TransactionResult processDepositOfTransfert(TransferCommand cmd, TransactionCurrencyCode transactionCurrencyCode){
        Account targetAccount = transactionOperationExecutor.getAccountByAccountNumber(cmd.targetAccountNumber());

        SoldBeforeTransaction soldBeforeTransaction = new SoldBeforeTransaction(targetAccount.getBalance().value());

        BigDecimal exchangeRate = transactionOperationExecutor.getExchangeRate(
                transactionCurrencyCode.value(), targetAccount.getCurrencyCode().value()
        );
        FinalAmount finalAmount = transactionOperationExecutor.convertToFinalAmount(cmd.originalAmount().value(), exchangeRate);
        targetAccount.makeDeposit(finalAmount);
        targetAccount = transactionOperationExecutor.updateAndSaveAccount(targetAccount);

        CreateTransactionCommand transactionCommand = transactionOperationExecutor.prepareTransactionCommand(
                targetAccount,
                transactionCurrencyCode,
                TransactionTypeEnum.DEPOSIT,
                exchangeRate,
                cmd.originalAmount(),
                cmd.userInfo().username(),
                soldBeforeTransaction,
                cmd.description()
        );
        Transaction depositTransaction = createTransactionUseCase.execute(transactionCommand);

        return TransactionResult.builder()
                .account(targetAccount)
                .transaction(depositTransaction)
                .build();
    }

    private void verifySourceAccountProperty(UserId userId, Account sourceAccount){
        CustomerId connectedCustomerId = customerLinkerPort.findCustomerIdByUserId(userId);
        if(Objects.isNull(connectedCustomerId) || !Objects.equals(sourceAccount.getCustomerId(), connectedCustomerId)){
            throw new OperationNotPermittedException("Transfert interrompue: le compte source ne vous appartient pas");
        }
    }
}
