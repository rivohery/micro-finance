package com.alibou.finance.account.infrastructure.adapter.out.persistence.entity;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import com.alibou.finance.currency.infrastructure.adapter.out.entity.CurrencyEntity;
import com.alibou.finance.shared.infrastructure.entity.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="accounts")
public class AccountEntity extends BaseAuditingEntity {

    @Column(nullable = false, unique = true)
    private String accountNumber;
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal balance;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal mgaBalance;
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal overdraftLimit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatusEnum accountStatus;
    @ManyToOne
    @JoinColumn(name="currency_id")
    private CurrencyEntity currencyEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_type_id")
    private AccountTypeEntity accountTypeEntity;
    @Column(nullable = false)
    private UUID customerId;

}

