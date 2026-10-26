package com.alibou.finance.history.application.service;

import com.alibou.finance.shared.application.command.InterestTraceRateCommand;
import com.alibou.finance.history.application.port.usecase.InterestRateUseCase;
import com.alibou.finance.history.domain.agregate.InterestRateTrace;
import com.alibou.finance.history.domain.out.repository.InterestRateTraceRepository;
import com.alibou.finance.shared.application.result.PageResult;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class InterestRateServiceApplication implements InterestRateUseCase {

    private final InterestRateTraceRepository interestRateTraceRepository;

    @Override
    public InterestRateTrace save(InterestTraceRateCommand cmd) {
        InterestRateTrace newInterestRateTrace = InterestRateTrace.initialize();
        newInterestRateTrace.buildFromCommand(cmd);
        return interestRateTraceRepository.save(newInterestRateTrace);
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
