package com.alibou.finance.account.application.utils;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.exception.AccountNotFoundException;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.service.ReferenceGenerator;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.log.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.log.domain.vo.transaction.*;
import com.alibou.finance.shared.vo.domain.Description;
import com.alibou.finance.shared.vo.domain.OperatorName;

import java.math.BigDecimal;

public class TransactionFactory {
    private static final int REFERENCE_LENGTH = 6;

    public static Account getAccountByAccountNumber(AccountRepository accountRepository, AccountNumber accountNumber){
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new AccountNotFoundException(String.format("Compte introuvable: numéros de compte invalid: %s", accountNumber.value()))
        );
    }

    public static FinalAmount convertToFinalAmount(BigDecimal originalAmount, BigDecimal exchangeRate){
        return new FinalAmount(originalAmount.multiply(exchangeRate));
    }

    public static void updateMgaBalance(CurrencyExchangePort currencyExchangePort, Account account){
        BigDecimal exchangeRateToMga = currencyExchangePort.getExchangeRate(account.getCurrency().getCode().value(), "MGA");
        account.calculMgaBalance(exchangeRateToMga);
    }

    public static Transaction prepareTraceOfTransaction(
            ReferenceGenerator referenceGenerator,
            AccountNumber accountNumber,
            TransactionCurrencyCode transactionCurrencyCode,
            TransactionTypeEnum transactionTypeEnum,
            FinalAmount finalAmount,
            BigDecimal exchangeRateValue,
            String targetCurrencyCodeRaw,
            SoldBeforeTransaction soldBeforeTransaction,
            User user,
            Description description,
            OriginalAmount originalAmount
    ){
        String generatedValue = referenceGenerator.generateReferenceCharacter(REFERENCE_LENGTH);
        TransactionType transactionType = new TransactionType(transactionTypeEnum);
        Reference reference = Transaction.generateReference(transactionType, generatedValue);
        TargetCurrencyCode targetCurrencyCode = new TargetCurrencyCode(targetCurrencyCodeRaw);
        ExchangeRate exchangeRate = new ExchangeRate(exchangeRateValue);
        OperatorName operatorName = new OperatorName(user.getUsername().value());

        return Transaction.initializeNewTransaction(
                transactionCurrencyCode,
                targetCurrencyCode,
                soldBeforeTransaction,
                transactionType,
                accountNumber,
                description,
                exchangeRate,
                originalAmount,
                operatorName,
                finalAmount,
                reference
        );
    }
}
