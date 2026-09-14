package com.alibou.finance.log.domain.agregate;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.log.domain.vo.interestRateTrace.InterestRateTraceId;
import com.alibou.finance.log.domain.vo.interestRateTrace.Amount;
import com.alibou.finance.log.domain.vo.interestRateTrace.MgaAmount;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class InterestRateTrace {
    private InterestRateTraceId interestRateTraceId;
    private Account account;
    private Amount amount;
    private MgaAmount mgaAmount;
    private String month;
    private String year;

    public static InterestRateTrace prepareToDataBase(Account account,BigDecimal mgaExchangeRate, BigDecimal monthlyInterestRate){
        LocalDateTime now = LocalDateTime.now();
        BigDecimal mgaAmount = monthlyInterestRate.multiply(mgaExchangeRate);
        return InterestRateTrace.builder()
                .interestRateTraceId(InterestRateTraceId.generate())
                .year("" + now.getYear())
                .month(now.getMonth().name())
                .mgaAmount(new MgaAmount(mgaAmount))
                .amount(new Amount(monthlyInterestRate))
                .account(account)
                .build();
    }

}


