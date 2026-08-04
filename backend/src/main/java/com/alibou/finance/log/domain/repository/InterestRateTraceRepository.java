package com.alibou.finance.log.domain.repository;

import com.alibou.finance.log.domain.agregate.InterestRateTrace;
import com.alibou.finance.shared.application.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface InterestRateTraceRepository {
    InterestRateTrace save(InterestRateTrace interestRateTrace);
    BigDecimal getTotalMonthlyInterestRate(String month, String year);
    PageResult<InterestRateTrace> findAllMonthlyInterestRateTrace(String month, String year, int page, int size);
}
