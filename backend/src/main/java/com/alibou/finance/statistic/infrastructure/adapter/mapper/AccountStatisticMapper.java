package com.alibou.finance.statistic.infrastructure.adapter.mapper;

import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.NumberAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.SoldeAccountStatisticProj;
import com.alibou.finance.statistic.domain.agregate.NumberAccountStatistic;
import com.alibou.finance.statistic.domain.agregate.SoldeAccountStatistic;

public class AccountStatisticMapper {
    public static NumberAccountStatistic ofNumberAccount(NumberAccountStatisticProj projection){
        return NumberAccountStatistic
                .builder()
                .accountType(projection.getAccountType())
                .nbrAccountByType(projection.getNbrAccountByType())
                .build();
    }

    public static SoldeAccountStatistic ofSoldAccount(SoldeAccountStatisticProj projection){
        return SoldeAccountStatistic
                .builder()
                .soldeAccountByType(projection.getSoldeAccountByType())
                .accountType(projection.getAccountType())
                .build();
    }
}
