package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.dto.input.TransactionInput;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.customer.application.port.CustomerConsultationUseCase;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.log.domain.vo.transaction.*;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import com.alibou.finance.shared.vo.domain.OperatorName;
import com.alibou.finance.account.application.port.usecase.AccountTransactionUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.log.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.account.domain.exception.AccountNotFoundException;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.service.ReferenceGenerator;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationPort;
import com.alibou.finance.account.domain.out.service.dto.TransfertConfirmationInfo;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class AccountTransactionServiceApplication implements AccountTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CurrencyExchangePort currencyExchangePort;
    private final CustomerConsultationUseCase customerService;
    private final ReferenceGenerator referenceGenerator;
    private final TransfertConfirmationPort transfertConfirmationService;
    private static final int REFERENCE_LENGTH = 6;



    @Override
    public Map<String, Object> deposit(TransactionInput input) {
        var account = getAccountByAccountNumber(input.getConcernedAccountNumber());

        /***Pour le calcul de taux d'intérêt***/
        SoldBeforeTransaction soldBeforeTransaction = SoldBeforeTransaction.getFrom(account.getBalance().value());

        BigDecimal exchangeRate = currencyExchangePort.getExchangeRate(input.getTransactionCurrencyCode().value(), account.getCurrency().getCode().value());
        FinalAmount finalAmount = convertToFinalAmount(input.getOriginalAmount().value(), exchangeRate);
        account.updateBalanceOfDeposit(finalAmount);
        updateMgaBalance(account);
        account = accountRepository.save(account);

        Transaction deposit = prepareAndSaveTraceOfTransaction(
                input,
                TransactionTypeEnum.DEPOSIT,
                finalAmount,
                exchangeRate,
                account.getCurrency().getCode().value(),
                soldBeforeTransaction
        );
        return Map.of(
                "account", account,
                "transaction", deposit
        );
    }

    @Override
    public Map<String, Object> withdraw(TransactionInput input) {
        var account = getAccountByAccountNumber(input.getConcernedAccountNumber());

        SoldBeforeTransaction soldBeforeTransaction = SoldBeforeTransaction.getFrom(account.getBalance().value());

        BigDecimal exchangeRate = currencyExchangePort.getExchangeRate(input.getTransactionCurrencyCode().value(), account.getCurrency().getCode().value());
        FinalAmount finalAmount = convertToFinalAmount(input.getOriginalAmount().value(), exchangeRate);

        account.updateBalanceOfWithdraw(finalAmount);
        updateMgaBalance(account);
        account = accountRepository.save(account);

        Transaction withdraw = prepareAndSaveTraceOfTransaction(
                input,
                TransactionTypeEnum.WITHDRAWAL,
                finalAmount,
                exchangeRate,
                account.getCurrency().getCode().value(),
                soldBeforeTransaction
        );
        return Map.of(
                "account", account,
                "transaction", withdraw
        );
    }

    @Override
    public Map<String, Object> transfert(TransactionInput input) {
        Map<String, Object> withdrawResult = processWithdrawOfTransfert(input);
        Account sourceAccount = (Account) withdrawResult.get("account");
        Transaction withdrawTransaction = (Transaction) withdrawResult.get("transaction");

        input.buildTransactionCurrencyCodeFrom(sourceAccount.getCurrency().getCode().value());
        Map<String, Object> depositResult = processDepositOfTransfert(input);
        Account targetAccount = (Account) depositResult.get("account");
        Transaction depositTransaction = (Transaction) depositResult.get("transaction");

        //On envoie une confirmation de transfert chez client
        TransfertConfirmationInfo transfertConfirmation = TransfertConfirmationInfo.initializeNewInfo(
                sourceAccount.getAccountNumber().value(),
                targetAccount.getAccountNumber().value(),
                sourceAccount.getCurrency().getCode().value(),
                targetAccount.getCurrency().getCode().value(),
                depositTransaction.getExchangeRate().value(),
                depositTransaction.getFinalAmount().value(),
                withdrawTransaction.getOriginalAmount().value(),
                input.getUser()
        );

        int resp = transfertConfirmationService.sendTransactionConfirmation(transfertConfirmation);
        log.info("Email confirmation sent : {}", resp);

        return Map.of(
                "sourceAccount", sourceAccount,
                "targetAccount", targetAccount,
                "withdrawTransaction", withdrawTransaction,
                "depositTransaction", depositTransaction
        );
    }

    private Account getAccountByAccountNumber(AccountNumber accountNumber){
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new AccountNotFoundException("Compte introuvable: numéros de compte invalid")
        );
    }

    private FinalAmount convertToFinalAmount(BigDecimal originalAmount, BigDecimal exchangeRate){
        return new FinalAmount(originalAmount.multiply(exchangeRate));
    }



    private Map<String, Object>processWithdrawOfTransfert(TransactionInput input){
        Account sourceAccount = getAccountByAccountNumber(input.getConcernedAccountNumber());

        SoldBeforeTransaction soldBeforeTransaction = new SoldBeforeTransaction(sourceAccount.getBalance().value());

        //On vérifie si le compte source appartient bien à l'utilisateur connecté
        verifySourceAccountProperty(input.getUser(), sourceAccount);

        input.buildTransactionCurrencyCodeFrom(sourceAccount.getCurrency().getCode().value());

        // => exchangeRate = 1(la monnaie de la transaction est celle de la monnaie source)
        BigDecimal exchangeRate = BigDecimal.ONE;
        // => finalAmount : identique au montant du transfert
        FinalAmount finalAmount = new FinalAmount(input.getOriginalAmount().value());

        sourceAccount.updateBalanceOfWithdraw(finalAmount);
        updateMgaBalance(sourceAccount);
        sourceAccount = accountRepository.save(sourceAccount);

        Transaction withdrawTransaction = prepareAndSaveTraceOfTransaction(
                input,
                TransactionTypeEnum.WITHDRAWAL,
                finalAmount,
                exchangeRate,
                sourceAccount.getCurrency().getCode().value(),
                soldBeforeTransaction
        );
        return Map.of(
                "account", sourceAccount,
                "transaction", withdrawTransaction
        );
    }

    private Map<String, Object>processDepositOfTransfert(TransactionInput input){
        Account targetAccount = getAccountByAccountNumber(input.getTargetAccountNumber());

        SoldBeforeTransaction soldBeforeTransaction = new SoldBeforeTransaction(targetAccount.getBalance().value());

        BigDecimal exchangeRate = currencyExchangePort.getExchangeRate(input.getTransactionCurrencyCode().value(), targetAccount.getCurrency().getCode().value());
        FinalAmount finalAmount = convertToFinalAmount(input.getOriginalAmount().value(), exchangeRate);
        targetAccount.updateBalanceOfDeposit(finalAmount);
        updateMgaBalance(targetAccount);
        targetAccount = accountRepository.save(targetAccount);

        input.updateConcernedAccountNumber(targetAccount.getAccountNumber());
        Transaction depositTransaction = prepareAndSaveTraceOfTransaction(
                input,
                TransactionTypeEnum.DEPOSIT,
                finalAmount,
                exchangeRate,
                targetAccount.getCurrency().getCode().value(),
                soldBeforeTransaction
        );
        return Map.of(
                "account", targetAccount,
                "transaction", depositTransaction
        );
    }

    private void verifySourceAccountProperty(User connectedUser, Account sourceAccount){
        CustomerId connectedCustomerId = customerService.findCustomerIdByUser(connectedUser);
        if(Objects.isNull(connectedCustomerId) || !Objects.equals(sourceAccount.getCustomerId().value(), connectedCustomerId.value())){
            throw new OperationNotPermittedException("Transfert interrompue car le compte source appartient à d'autre client");
        }
    }

    private void updateMgaBalance(Account account){
        BigDecimal exchangeRateToMga = currencyExchangePort.getExchangeRate(account.getCurrency().getCode().value(), "MGA");
        account.calculMgaBalance(exchangeRateToMga);
    }

    private Transaction prepareAndSaveTraceOfTransaction(
            TransactionInput transactionInput,
            TransactionTypeEnum transactionTypeEnum,
            FinalAmount finalAmount,
            BigDecimal exchangeRateValue,
            String targetCurrencyCodeRaw,
            SoldBeforeTransaction soldBeforeTransaction
    ){
        String generatedValue = referenceGenerator.generateReferenceCharacter(REFERENCE_LENGTH);
        TransactionType transactionType = new TransactionType(transactionTypeEnum);
        Reference reference = Transaction.generateReference(transactionType, generatedValue);
        TargetCurrencyCode targetCurrencyCode = new TargetCurrencyCode(targetCurrencyCodeRaw);
        ExchangeRate exchangeRate = new ExchangeRate(exchangeRateValue);
        OperatorName operatorName = new OperatorName(transactionInput.getUser().getUsername().value());

        Transaction transaction = Transaction.initializeNewTransaction(
                transactionInput.getTransactionCurrencyCode(),
                targetCurrencyCode,
                soldBeforeTransaction,
                transactionType,
                transactionInput.getConcernedAccountNumber(),
                transactionInput.getDescription(),
                exchangeRate,
                transactionInput.getOriginalAmount(),
                operatorName,
                finalAmount,
                reference
        );
        return transactionRepository.save(transaction);
    }
}


