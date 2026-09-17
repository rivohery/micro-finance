package com.alibou.finance.history.infrastructure.adapter.out.service;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.history.domain.agregate.InterestRateTrace;
import com.alibou.finance.history.domain.out.service.InterestRateTraceFactory;
import com.alibou.finance.history.domain.vo.interestRateTrace.Amount;
import com.alibou.finance.history.domain.vo.interestRateTrace.InterestRateTraceId;
import com.alibou.finance.history.domain.vo.interestRateTrace.MgaAmount;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class InterestRateTraceFactoryAdapter implements InterestRateTraceFactory {

    @Override
    public InterestRateTrace prepare(Account account, BigDecimal mgaExchangeRate, BigDecimal monthlyInterestRate) {
        Objects.requireNonNull(account, "account must not be null");
        Objects.requireNonNull(mgaExchangeRate, "mgaExchangeRate must not be null");
        Objects.requireNonNull(monthlyInterestRate, "monthlyInterestRate must not be null");
        if(monthlyInterestRate.compareTo(BigDecimal.ZERO) > 0 ){
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
        return null;
    }
}
