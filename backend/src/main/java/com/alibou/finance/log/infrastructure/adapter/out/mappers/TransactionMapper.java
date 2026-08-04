package com.alibou.finance.log.infrastructure.adapter.out.mappers;

import com.alibou.finance.log.domain.vo.transaction.*;
import com.alibou.finance.shared.vo.domain.OperatorName;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.log.infrastructure.adapter.in.dto.TransactionResponse;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.shared.vo.domain.Description;
import com.alibou.finance.log.infrastructure.adapter.out.persistence.entity.TransactionEntity;

public class TransactionMapper {

    public static Transaction entityToDomain(TransactionEntity entity){
        return Transaction.builder()
                .transactionId(TransactionId.from(entity.getId()))
                .accountNumber(new AccountNumber(entity.getAccountNumber()))
                .description(new Description(entity.getDescription()))
                .reference(new Reference(entity.getReference()))
                .transactionType(new TransactionType(entity.getTransactionType()))
                .createdDate(entity.getCreatedDate())
                .exchangeRate(new ExchangeRate(entity.getExchangeRate()))
                .originalAmount(new OriginalAmount(entity.getOriginalAmount()))
                .finalAmount(new FinalAmount(entity.getFinalAmount()))
                .operatorName(new OperatorName(entity.getOperatorName()))
                .soldBeforeTransaction(new SoldBeforeTransaction(entity.getSoldBeforeTransaction()))
                .transactionCurrencyCode(new TransactionCurrencyCode(entity.getTransactionCurrencyCode()))
                .targetCurrencyCode(new TargetCurrencyCode(entity.getTargetCurrencyCode()))
                .build();
    }

    public static TransactionEntity domainToEntity(Transaction domain){
        return TransactionEntity.builder()
                .id(domain.getTransactionId().value())
                .accountNumber(domain.getAccountNumber().value())
                .originalAmount(domain.getOriginalAmount().value())
                .finalAmount(domain.getFinalAmount().value())
                .exchangeRate(domain.getExchangeRate().value())
                .transactionCurrencyCode(domain.getTransactionCurrencyCode().value())
                .targetCurrencyCode(domain.getTargetCurrencyCode().value())
                .description(domain.getDescription().value())
                .reference(domain.getReference().value())
                .soldBeforeTransaction(domain.getSoldBeforeTransaction().value())
                .transactionType(domain.getTransactionType().value())
                .operatorName(domain.getOperatorName().value())
                .build();
    }

    public static TransactionResponse domainToResponse(Transaction domain){
        return TransactionResponse.builder()
                .accountNumber(domain.getAccountNumber().value())
                .createdDate(domain.getCreatedDate())
                .description(domain.getDescription().value())
                .exchangeRate(domain.getExchangeRate().value())
                .finalAmount(domain.getFinalAmount().value())
                .id(domain.getTransactionId().value())
                .originalAmount(domain.getOriginalAmount().value())
                .reference(domain.getReference().value())
                .transactionCurrency(domain.getTransactionCurrencyCode().value())
                .targetCurrency(domain.getTargetCurrencyCode().value())
                .operatorName(domain.getOperatorName().value())
                .transactionType(domain.getTransactionType().value())
                .build();
    }
}
