package com.alibou.finance.account.infrastructure.adapter.out.persistence.entity;

import com.alibou.finance.account.domain.agregate.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class TransactionEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String accountNumber;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false, unique = true)
    private String reference;
    @Column(nullable = false)
    private BigDecimal originalAmount;
    @Column(nullable = false)
    private BigDecimal finalAmount;
    @Column(nullable = false)
    private BigDecimal exchangeRate;
    @Column(name="transaction_currency")
    private String transactionCurrencyCode;//currency code : MGA|USD|EUR

    @Column(name="target_currency")
    private String targetCurrencyCode;
    @Column(nullable = false)
    private String operatorName;
    @Column(nullable = false)
    private BigDecimal soldBeforeTransaction;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;
}
