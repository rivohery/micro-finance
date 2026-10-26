package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.shared.infrastructure.dto.NumberAccountStatistic;
import com.alibou.finance.shared.infrastructure.dto.SoldeAccountStatistic;

import java.math.BigDecimal;
import java.util.List;

public interface AccountStatisticUseCase {
    List<NumberAccountStatistic> getAccountStatisticNumber();
    List<SoldeAccountStatistic> getAccountStatisticSold();
    BigDecimal getSoldeTotalOfAccountInMga();
    Long getNbrTotalOfAccount();

}
