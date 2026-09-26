package com.alibou.finance.statistic.domain.out;

import com.alibou.finance.shared.infrastructure.dto.RegistrationStatistic;

import java.time.LocalDate;
import java.util.List;

public interface CustomerStatisticLinker {
    Long getNbrTotalOfCustomer();
    List<RegistrationStatistic> getRegistrationStatisticsByWeek(LocalDate startWeek, LocalDate endWeek);
}
