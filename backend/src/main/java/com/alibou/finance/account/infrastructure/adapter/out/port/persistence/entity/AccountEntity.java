package com.alibou.finance.account.infrastructure.adapter.out.port.persistence.entity;

import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
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
    @Column(nullable = false, unique = true, name="currency_id")
    private UUID currencyId;

    @Column(nullable = false, name="currency_code")
    private String currencyCode;

    @Column(nullable = false, name="account_type_id")
    private UUID accountTypeId;

    @Column(nullable = false, name="account_type_name")
    private String accountTypeName;

    @Column(nullable = false, name="account_type_code")
    private String accountTypeCode;
    @Column(nullable = false, name="annual_interest_rate", precision = 19, scale = 4)
    private BigDecimal annualInterestRate;
    @Column(nullable = false)
    private UUID customerId;
    @Version
    private Long version;

}

