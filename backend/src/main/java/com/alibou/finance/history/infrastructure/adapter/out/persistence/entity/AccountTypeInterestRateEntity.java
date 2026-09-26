package com.alibou.finance.history.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name="account_type_interest_rate")
public class AccountTypeInterestRateEntity{
    @Id
    private UUID id;

    @Column(nullable = false, name="account_type_id")
    private UUID accountTypeId;

    @Column(nullable = false, name="account_type_code")
    private String accountTypeCode;

    @Column(nullable = false, name="annual_interest_rate")
    private BigDecimal annualInterestRate;
    @Column(nullable = false)
    private String annee;


}
