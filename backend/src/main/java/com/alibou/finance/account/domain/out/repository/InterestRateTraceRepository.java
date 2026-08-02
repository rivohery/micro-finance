package com.alibou.finance.account.domain.out.repository;

import com.alibou.finance.account.domain.agregate.InterestRateTrace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface InterestRateTraceRepository {
    InterestRateTrace save(InterestRateTrace interestRateTrace);
    BigDecimal getTotalMonthlyInterestRate(String month, String year);
    Page<InterestRateTrace>findAllMonthlyInterestRateTrace(String month, String year, Pageable pageable);
}
