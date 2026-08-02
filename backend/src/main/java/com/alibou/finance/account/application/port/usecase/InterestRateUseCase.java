package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.domain.agregate.InterestRateTrace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface InterestRateUseCase {
    InterestRateTrace save(InterestRateTrace interestRateTrace);
    BigDecimal getTotalMonthlyInterestRate(String month);
    Page<InterestRateTrace> findAllMonthlyInterestRateTrace(String month, Pageable pageable);
}
