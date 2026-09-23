package com.alibou.finance.history.domain.out.repository;

import com.alibou.finance.history.domain.agregate.InterestRateTrace;
import com.alibou.finance.shared.application.PageResult;

import java.math.BigDecimal;

public interface InterestRateTraceRepository {
    InterestRateTrace save(InterestRateTrace interestRateTrace);
    BigDecimal getTotalMonthlyInterestRate(String month, String year);
    PageResult<InterestRateTrace> findAllMonthlyInterestRateTrace(String month, String year, int page, int size);
}
