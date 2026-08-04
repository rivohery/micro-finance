package com.alibou.finance.log.application.port.usecase;

import com.alibou.finance.log.domain.agregate.InterestRateTrace;
import com.alibou.finance.shared.application.PageResult;

import java.math.BigDecimal;

public interface InterestRateUseCase {
    InterestRateTrace save(InterestRateTrace interestRateTrace);
    BigDecimal getTotalMonthlyInterestRate(String month);
    PageResult<InterestRateTrace> findAllMonthlyInterestRateTrace(String month, int page, int size);
}
