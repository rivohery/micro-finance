package com.alibou.finance.log.application.service;

import com.alibou.finance.log.application.port.usecase.InterestRateUseCase;
import com.alibou.finance.log.domain.agregate.InterestRateTrace;
import com.alibou.finance.log.domain.repository.InterestRateTraceRepository;
import com.alibou.finance.shared.application.PageResult;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class InterestRateServiceApplication implements InterestRateUseCase {

    private final InterestRateTraceRepository interestRateTraceRepository;

    @Override
    public InterestRateTrace save(InterestRateTrace interestRateTrace) {
        return interestRateTraceRepository.save(interestRateTrace);
    }

    @Override
    public BigDecimal getTotalMonthlyInterestRate(String month) {
        String year = "" + LocalDate.now().getYear();
        if(month == null || month.isBlank()){
            month = LocalDate.now().getMonth().name();
        }
        return interestRateTraceRepository.getTotalMonthlyInterestRate(month, year);
    }

    @Override
    public PageResult<InterestRateTrace> findAllMonthlyInterestRateTrace(String month, int page, int size) {
        String year = "" + LocalDate.now().getYear();
        if(month == null || month.isBlank()){
            month = LocalDate.now().getMonth().name();
        }
        return interestRateTraceRepository.findAllMonthlyInterestRateTrace(month, year, page, size);
    }
}
