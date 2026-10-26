package com.alibou.finance.history.internal;

import com.alibou.finance.history.HistoryTraceProvider;
import com.alibou.finance.shared.application.command.InterestTraceRateCommand;
import com.alibou.finance.history.application.port.usecase.InterestRateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryTraceProviderImpl implements HistoryTraceProvider {

    private final InterestRateUseCase interestRateUseCase;

    @Override
    public void saveMonthlyInterestRate(InterestTraceRateCommand cmd) {
        interestRateUseCase.save(cmd);
    }

}
