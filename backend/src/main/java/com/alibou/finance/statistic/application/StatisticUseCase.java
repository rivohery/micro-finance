package com.alibou.finance.statistic.application;

import com.alibou.finance.statistic.domain.agregate.NumberAccountStatistic;
import com.alibou.finance.statistic.domain.agregate.RegistrationStatistic;
import com.alibou.finance.statistic.domain.agregate.SoldeAccountStatistic;

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
