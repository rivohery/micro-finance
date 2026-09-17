package com.alibou.finance.history.application.port.usecase;

import com.alibou.finance.history.domain.agregate.InterestRateTrace;
import com.alibou.finance.shared.application.PageResult;

import java.math.BigDecimal;

public interface InterestRateUseCase {
    InterestRateTrace save(InterestRateTrace interestRateTrace);
    BigDecimal getTotalMonthlyInterestRate(String month);
    PageResult<InterestRateTrace> findAllMonthlyInterestRateTrace(String month, int page, int size);
}
