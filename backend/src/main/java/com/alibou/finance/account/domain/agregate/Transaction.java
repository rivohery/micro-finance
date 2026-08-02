package com.alibou.finance.account.domain.agregate;

import com.alibou.finance.account.domain.vo.transaction.*;
import com.alibou.finance.shared.vo.domain.OperatorName;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.shared.vo.domain.Description;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class Transaction {
    private TransactionId transactionId;
    private AccountNumber accountNumber;
    private OriginalAmount originalAmount;
    private FinalAmount finalAmount;
    private ExchangeRate exchangeRate;
    private TransactionCurrencyCode transactionCurrencyCode;//currency code : MGA|USD|EUR
    private TargetCurrencyCode targetCurrencyCode;
    private TransactionType transactionType;
    private Description description;
    private Reference reference;
    private OperatorName operatorName;
    private SoldBeforeTransaction soldBeforeTransaction;
    private LocalDateTime createdDate;

    public static Reference generateReference(TransactionType transactionType, String alphaNumericValue){
        //like: DEP-20260505-AB12RT67FG
        String type =  transactionType.value().name().substring(0, 3);
        String date =  LocalDate.now().toString().replace("-", "");
        return new Reference(String.format("%s-%s-%s", type, date, alphaNumericValue));
    }

    public void updateReference(Reference reference){
        this.reference = reference;
    }

    public static Transaction initializeNewTransaction(
            TransactionCurrencyCode transactionCurrencyCode,
            TargetCurrencyCode targetCurrencyCode,
            SoldBeforeTransaction soldBeforeTransaction,
            TransactionType transactionType,
            AccountNumber accountNumber,
            Description description,
            ExchangeRate exchangeRate,
            OriginalAmount originalAmount,
            OperatorName operatorName,
            FinalAmount finalAmount,
            Reference reference
    ){
        return Transaction.builder()
                .transactionId(TransactionId.generate())
                .transactionCurrencyCode(transactionCurrencyCode)
                .targetCurrencyCode(targetCurrencyCode)
                .soldBeforeTransaction(soldBeforeTransaction)
                .transactionType(transactionType)
                .accountNumber(accountNumber)
                .description(description)
                .exchangeRate(exchangeRate)
                .originalAmount(originalAmount)
                .operatorName(operatorName)
                .finalAmount(finalAmount)// montant en monnaie du compte concerné pour deposit|withdraw et compte cible pour le transfert
                .reference(reference)
                .build();
    }
}

