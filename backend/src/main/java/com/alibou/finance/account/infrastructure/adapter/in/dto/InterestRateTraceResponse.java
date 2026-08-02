package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.account.domain.agregate.InterestRateTrace;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterestRateTraceResponse {
    private UUID id;
    private String accountNumber;
    private BigDecimal interestRate;
    private String currencyCode;
    private BigDecimal amount;
    private BigDecimal mgaAmount;
    //January, February, March, April, May, June, July, August, September, October, November and December.
    private String month;
    private String year;

    public static InterestRateTraceResponse buildFromDomain(InterestRateTrace domain){
        return InterestRateTraceResponse.builder()
                .id(domain.getInterestRateTraceId().value())
                .accountNumber(domain.getAccount().getAccountNumber().value())
                .interestRate(domain.getAccount().getAccountType().getAnnualInterestRate().value())
                .currencyCode(domain.getAccount().getCurrency().getCode().value())
                .amount(domain.getAmount().value())
                .mgaAmount(domain.getMgaAmount().value())
                .month(domain.getMonth())
                .year(domain.getYear())
                .build();
    }
}
