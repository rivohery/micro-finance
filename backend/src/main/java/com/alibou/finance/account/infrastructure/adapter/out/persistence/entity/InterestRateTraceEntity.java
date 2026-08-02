package com.alibou.finance.account.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interest_rate_trace")
public class InterestRateTraceEntity {
    @Id
    private UUID id;
    @Column(nullable = false, name = "account_number")
    private String accountNumber;
    @Column(nullable = false, name = "interest_rate", precision = 19, scale = 4)
    private BigDecimal interestRate;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    @Column(nullable = false, name="currency_code")
    private String currencyCode;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal mgaAmount;

    //January, February, March, April, May, June, July, August, September, October, November and December.
    @Column(nullable = false, name = "col_month")
    private String month;
    @Column(nullable = false, name= "col_year")
    private String year;
}
