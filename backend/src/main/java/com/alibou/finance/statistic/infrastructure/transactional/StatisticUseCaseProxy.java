package com.alibou.finance.statistic.infrastructure.transactional;

import com.alibou.finance.statistic.application.StatisticUseCase;
import com.alibou.finance.statistic.domain.agregate.NumberAccountStatistic;
import com.alibou.finance.statistic.domain.agregate.RegistrationStatistic;
import com.alibou.finance.statistic.domain.agregate.SoldeAccountStatistic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticUseCaseProxy {

    private final StatisticUseCase statisticUseCase;

    @Transactional(readOnly = true)
    public List<NumberAccountStatistic> getAccountStatisticNumber() {
        return statisticUseCase.getAccountStatisticNumber();
    }

    @Transactional(readOnly = true)
    public List<SoldeAccountStatistic> getAccountStatisticSold() {
        return statisticUseCase.getAccountStatisticSold();
    }

    @Transactional(readOnly = true)
    public BigDecimal getSoldeTotalOfAccountInMga() {
        return statisticUseCase.getSoldeTotalOfAccountInMga();
    }

    @Transactional(readOnly = true)
    public Long getNbrTotalOfAccount() {
        return statisticUseCase.getNbrTotalOfAccount();
    }

    @Transactional(readOnly = true)
    public Long getNbrTotalOfCustomer() {
        return statisticUseCase.getNbrTotalOfCustomer();
    }

    @Transactional(readOnly = true)
    public List<RegistrationStatistic> getRegistrationStatisticsByWeek() {
        return statisticUseCase.getRegistrationStatisticsByWeek();
    }

}
