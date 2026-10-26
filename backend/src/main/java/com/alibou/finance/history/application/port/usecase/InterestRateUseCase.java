package com.alibou.finance.history.application.port.usecase;

import com.alibou.finance.shared.application.command.InterestTraceRateCommand;
import com.alibou.finance.history.domain.agregate.InterestRateTrace;
import com.alibou.finance.shared.application.result.PageResult;

import java.math.BigDecimal;

public interface InterestRateUseCase {
    InterestRateTrace save(InterestTraceRateCommand cmd);

    /**
     * La valeur totale du taux d'intérêts en MGA pour le mois donné
     * @param month
     * @return
     */
    BigDecimal getTotalMonthlyInterestRate(String month);

    PageResult<InterestRateTrace> findAllMonthlyInterestRateTrace(String month, int page, int size);
}
