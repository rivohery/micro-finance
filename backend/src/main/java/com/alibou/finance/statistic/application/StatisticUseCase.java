package com.alibou.finance.statistic.application;

import com.alibou.finance.shared.infrastructure.dto.NumberAccountStatistic;
import com.alibou.finance.shared.infrastructure.dto.RegistrationStatistic;
import com.alibou.finance.shared.infrastructure.dto.SoldeAccountStatistic;

import java.math.BigDecimal;
import java.util.List;

public interface StatisticUseCase {
    List<NumberAccountStatistic>getAccountStatisticNumber();
    List<SoldeAccountStatistic>getAccountStatisticSold();
    BigDecimal getSoldeTotalOfAccountInMga();
    Long getNbrTotalOfAccount();
    Long getNbrTotalOfCustomer();
    List<RegistrationStatistic> getRegistrationStatisticsByWeek();

}
