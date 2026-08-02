package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.account.domain.agregate.TransactionTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    private UUID id;
    private String accountNumber;
    private TransactionTypeEnum transactionType;
    private String description;
    private String reference;
    private BigDecimal originalAmount;
    private BigDecimal finalAmount;
    private BigDecimal exchangeRate;
    private String operatorName;
    private String transactionCurrency;
    private String targetCurrency;
    private LocalDateTime createdDate;
}
