package com.alibou.finance.account;

import com.alibou.finance.shared.infrastructure.dto.NumberAccountStatistic;
import com.alibou.finance.shared.infrastructure.dto.SoldeAccountStatistic;

import java.math.BigDecimal;
import java.util.List;

public interface AccountProvider {
    List<NumberAccountStatistic> getAccountStatisticNumber();
    List<SoldeAccountStatistic> getAccountStatisticSold();
    BigDecimal getSoldeTotalOfAccountInMga();
    Long getNbrTotalOfAccount();


}
