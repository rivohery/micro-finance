package com.alibou.finance.statistic.application;

import com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection.RegistrationStatisticProj;
import com.alibou.finance.statistic.domain.agregate.NumberAccountStatistic;
import com.alibou.finance.statistic.domain.agregate.RegistrationStatistic;
import com.alibou.finance.statistic.domain.agregate.SoldeAccountStatistic;
import com.alibou.finance.statistic.domain.service.AccountServicePort;
import com.alibou.finance.statistic.domain.service.CustomerServicePort;
import com.alibou.finance.statistic.infrastructure.adapter.mapper.AccountStatisticMapper;
import com.alibou.finance.statistic.infrastructure.adapter.mapper.RegistrationStatisticMapper;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class StatisticServiceApplication implements StatisticUseCase{

    private final AccountServicePort accountServicePort;
    private final CustomerServicePort customerServicePort;

    @Override
    public List<NumberAccountStatistic> getAccountStatisticNumber() {
        return accountServicePort.getAccountStatisticNumber()
                .stream()
                .map(AccountStatisticMapper::ofNumberAccount)
                .toList();
    }
    @Override
    public List<SoldeAccountStatistic> getAccountStatisticSold() {
        return accountServicePort.getAccountStatisticSold()
                .stream()
                .map(AccountStatisticMapper::ofSoldAccount)
                .toList();
    }
    @Override
    public BigDecimal getSoldeTotalOfAccountInMga() {
        return accountServicePort.getSoldeTotalOfAccountInMga();
    }
    @Override
    public Long getNbrTotalOfAccount() {
        return accountServicePort.getNbrTotalOfAccount();
    }
    @Override
    public Long getNbrTotalOfCustomer() {
        return customerServicePort.getNbrTotalOfCustomer();
    }
    @Override
    public List<RegistrationStatistic> getRegistrationStatisticsByWeek() {
        LocalDate today = LocalDate.now();

        LocalDate startWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate endWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return customerServicePort.getRegistrationStatisticsByWeek(startWeek, endWeek)
                .stream()
                .sorted(Comparator.comparing(RegistrationStatisticProj::getCreatedDate))
                .map(RegistrationStatisticMapper::fromProjection)
                .toList();
    }
}
