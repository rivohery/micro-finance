package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.dto.command.TransferCommand;
import com.alibou.finance.account.application.port.dto.output.TransactionResult;
import com.alibou.finance.account.application.port.dto.output.TransferResult;
import com.alibou.finance.account.application.port.usecase.TransferUseCase;
import com.alibou.finance.account.application.utils.TransactionFactory;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.service.ReferenceGenerator;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationPort;
import com.alibou.finance.account.domain.out.service.dto.TransfertConfirmationInfo;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.customer.application.port.CustomerConsultationUseCase;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.log.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.log.domain.vo.transaction.FinalAmount;
import com.alibou.finance.log.domain.vo.transaction.SoldBeforeTransaction;
import com.alibou.finance.log.domain.vo.transaction.TransactionCurrencyCode;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class TransferServiceApplication implements TransferUseCase {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CurrencyExchangePort currencyExchangePort;
    private final CustomerConsultationUseCase customerService;
    private final ReferenceGenerator referenceGenerator;
    private final TransfertConfirmationPort transfertConfirmationService;

    @Override
    public TransferResult execute(TransferCommand input) {
        TransactionResult withdrawResult = processWithdrawOfTransfert(input);
        Account sourceAccount = withdrawResult.account();
        Transaction withdrawTransaction = withdrawResult.transaction();

        TransactionCurrencyCode transactionCurrencyCode = new TransactionCurrencyCode(sourceAccount.getCurrency().getCode().value());

        TransactionResult depositResult = processDepositOfTransfert(input, transactionCurrencyCode);
        Account targetAccount = depositResult.account();
        Transaction depositTransaction = depositResult.transaction();

        //On envoie une confirmation de transfert chez client
        TransfertConfirmationInfo transfertConfirmation = TransfertConfirmationInfo.initializeNewInfo(
                sourceAccount.getAccountNumber().value(),
                targetAccount.getAccountNumber().value(),
                sourceAccount.getCurrency().getCode().value(),
                targetAccount.getCurrency().getCode().value(),
                depositTransaction.getExchangeRate().value(),
                depositTransaction.getFinalAmount().value(),
                withdrawTransaction.getOriginalAmount().value(),
                input.user()
        );

        int resp = transfertConfirmationService.sendTransactionConfirmation(transfertConfirmation);
        log.info("Email confirmation sent : {}", resp);

        return TransferResult.builder()
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .depositTransaction(depositTransaction)
                .withdrawTransaction(withdrawTransaction)
                .build();
    }

    private TransactionResult processWithdrawOfTransfert(TransferCommand input){
        Account sourceAccount = TransactionFactory.getAccountByAccountNumber(accountRepository, input.sourceAccountNumber());

        SoldBeforeTransaction soldBeforeTransaction = new SoldBeforeTransaction(sourceAccount.getBalance().value());

        //On vérifie si le compte source appartient bien à l'utilisateur connecté
        verifySourceAccountProperty(input.user(), sourceAccount);

        // => exchangeRate = 1(la monnaie de la transaction est celle de la monnaie source)
        BigDecimal exchangeRate = BigDecimal.ONE;
        // => finalAmount : identique au montant du transfert

        FinalAmount finalAmount = new FinalAmount(input.originalAmount().value());
        sourceAccount.updateBalanceOfWithdraw(finalAmount);
        TransactionFactory.updateMgaBalance(currencyExchangePort, sourceAccount);
        sourceAccount = accountRepository.save(sourceAccount);

        TransactionCurrencyCode transactionCurrencyCode = new TransactionCurrencyCode(sourceAccount.getCurrency().getCode().value());
        Transaction withdrawTransaction = TransactionFactory.prepareTraceOfTransaction(
                referenceGenerator,
                sourceAccount.getAccountNumber(),
                transactionCurrencyCode,
                TransactionTypeEnum.WITHDRAWAL,
                finalAmount,
                exchangeRate,
                sourceAccount.getCurrency().getCode().value(),
                soldBeforeTransaction,
                input.user(),
                input.description(),
                input.originalAmount()
        );
        withdrawTransaction = transactionRepository.save(withdrawTransaction);
        return TransactionResult.builder()
                .account(sourceAccount)
                .transaction(withdrawTransaction)
                .build();
    }

    private TransactionResult processDepositOfTransfert(TransferCommand input, TransactionCurrencyCode transactionCurrencyCode){
        Account targetAccount = TransactionFactory.getAccountByAccountNumber(accountRepository, input.targetAccountNumber());

        SoldBeforeTransaction soldBeforeTransaction = new SoldBeforeTransaction(targetAccount.getBalance().value());

        BigDecimal exchangeRate = currencyExchangePort.getExchangeRate(transactionCurrencyCode.value(), targetAccount.getCurrency().getCode().value());
        FinalAmount finalAmount = TransactionFactory.convertToFinalAmount(input.originalAmount().value(), exchangeRate);
        targetAccount.updateBalanceOfDeposit(finalAmount);
        TransactionFactory.updateMgaBalance(currencyExchangePort, targetAccount);
        targetAccount = accountRepository.save(targetAccount);

        Transaction depositTransaction = TransactionFactory.prepareTraceOfTransaction(
                referenceGenerator,
                targetAccount.getAccountNumber(),
                transactionCurrencyCode,
                TransactionTypeEnum.DEPOSIT,
                finalAmount,
                exchangeRate,
                targetAccount.getCurrency().getCode().value(),
                soldBeforeTransaction,
                input.user(),
                input.description(),
                input.originalAmount()
        );

        depositTransaction = transactionRepository.save(depositTransaction);

        return TransactionResult.builder()
                .account(targetAccount)
                .transaction(depositTransaction)
                .build();
    }

    private void verifySourceAccountProperty(User connectedUser, Account sourceAccount){
        CustomerId connectedCustomerId = customerService.findCustomerIdByUser(connectedUser);
        if(Objects.isNull(connectedCustomerId) || !Objects.equals(sourceAccount.getCustomerId().value(), connectedCustomerId.value())){
            throw new OperationNotPermittedException("Transfert interrompue: le compte source ne vous appartient pas");
        }
    }
}
