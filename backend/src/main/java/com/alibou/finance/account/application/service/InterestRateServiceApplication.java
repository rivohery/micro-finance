package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.usecase.InterestRateUseCase;
import com.alibou.finance.account.domain.agregate.InterestRateTrace;
import com.alibou.finance.account.domain.out.repository.InterestRateTraceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InterestRateServiceApplication implements InterestRateUseCase {

    private final InterestRateTraceRepository interestRateTraceRepository;

    @Override
    @Transactional
    public InterestRateTrace save(InterestRateTrace interestRateTrace) {
        return interestRateTraceRepository.save(interestRateTrace);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalMonthlyInterestRate(String month) {
        String year = "" + LocalDate.now().getYear();
        if(month == null || month.isBlank()){
            month = LocalDate.now().getMonth().name();
        }
        return interestRateTraceRepository.getTotalMonthlyInterestRate(month, year);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InterestRateTrace> findAllMonthlyInterestRateTrace(String month, Pageable pageable) {
        String year = "" + LocalDate.now().getYear();
        if(month == null || month.isBlank()){
            month = LocalDate.now().getMonth().name();
        }
        return interestRateTraceRepository.findAllMonthlyInterestRateTrace(month, year, pageable);
    }
}
