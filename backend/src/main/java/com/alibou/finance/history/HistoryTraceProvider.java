package com.alibou.finance.history;

import com.alibou.finance.shared.application.command.InterestTraceRateCommand;

public interface HistoryTraceProvider {
    void saveMonthlyInterestRate(InterestTraceRateCommand cmd);

}
