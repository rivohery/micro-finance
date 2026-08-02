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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceApplication implements StatisticUseCase{

    private final AccountServicePort accountServicePort;
    private final CustomerServicePort customerServicePort;

    @Override
    @Transactional(readOnly = true)
    public List<NumberAccountStatistic> getAccountStatisticNumber() {
        return accountServicePort.getAccountStatisticNumber()
                .stream()
                .map(AccountStatisticMapper::ofNumberAccount)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<SoldeAccountStatistic> getAccountStatisticSold() {
        return accountServicePort.getAccountStatisticSold()
                .stream()
                .map(AccountStatisticMapper::ofSoldAccount)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getSoldeTotalOfAccountInMga() {
        return accountServicePort.getSoldeTotalOfAccountInMga();
    }
    @Override
    @Transactional(readOnly = true)
    public Long getNbrTotalOfAccount() {
        return accountServicePort.getNbrTotalOfAccount();
    }
    @Override
    @Transactional(readOnly = true)
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
