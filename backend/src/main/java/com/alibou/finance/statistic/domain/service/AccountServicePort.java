package com.alibou.finance.statistic.domain.service;

import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.NumberAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.SoldeAccountStatisticProj;

import java.math.BigDecimal;
import java.util.List;

public interface AccountServicePort {
    List<NumberAccountStatisticProj> getAccountStatisticNumber();
    List<SoldeAccountStatisticProj>getAccountStatisticSold();
    BigDecimal getSoldeTotalOfAccountInMga();
    Long getNbrTotalOfAccount();
}
