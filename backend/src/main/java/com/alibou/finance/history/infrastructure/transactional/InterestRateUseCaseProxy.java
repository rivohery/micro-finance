package com.alibou.finance.history.infrastructure.transactional;

import com.alibou.finance.history.application.port.usecase.InterestRateUseCase;
import com.alibou.finance.history.domain.agregate.InterestRateTrace;
import com.alibou.finance.shared.application.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InterestRateUseCaseProxy {

    private final InterestRateUseCase interestRateUseCase;

    @Transactional
    public InterestRateTrace save(InterestRateTrace interestRateTrace) {
        return interestRateUseCase.save(interestRateTrace);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalMonthlyInterestRate(String month) {
        return interestRateUseCase.getTotalMonthlyInterestRate(month);
    }

    @Transactional(readOnly = true)
    public PageResult<InterestRateTrace> findAllMonthlyInterestRateTrace(String month, int page, int size) {
        return interestRateUseCase.findAllMonthlyInterestRateTrace(month, page, size);
    }

}
