package com.alibou.finance.statistic.application;

import com.alibou.finance.shared.infrastructure.dto.NumberAccountStatistic;
import com.alibou.finance.shared.infrastructure.dto.RegistrationStatistic;
import com.alibou.finance.shared.infrastructure.dto.SoldeAccountStatistic;
import com.alibou.finance.statistic.domain.out.AccountStatisticLinker;
import com.alibou.finance.statistic.domain.out.CustomerStatisticLinker;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RequiredArgsConstructor
public class StatisticServiceApplication implements StatisticUseCase{

    private final AccountStatisticLinker accountStatisticLinker;
    private final CustomerStatisticLinker customerStatisticLinker;

    @Override
    public List<NumberAccountStatistic> getAccountStatisticNumber() {
        return accountStatisticLinker.getAccountStatisticNumber();
    }
    @Override
    public List<SoldeAccountStatistic> getAccountStatisticSold() {
        return accountStatisticLinker.getAccountStatisticSold();
    }
    @Override
    public BigDecimal getSoldeTotalOfAccountInMga() {
        return accountStatisticLinker.getSoldeTotalOfAccountInMga();
    }
    @Override
    public Long getNbrTotalOfAccount() {
        return accountStatisticLinker.getNbrTotalOfAccount();
    }
    @Override
    public Long getNbrTotalOfCustomer() {
        return customerStatisticLinker.getNbrTotalOfCustomer();
    }
    @Override
    public List<RegistrationStatistic> getRegistrationStatisticsByWeek() {
        LocalDate today = LocalDate.now();

        LocalDate startWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate endWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return customerStatisticLinker.getRegistrationStatisticsByWeek(startWeek, endWeek);
    }
}
