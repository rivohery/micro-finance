package com.alibou.finance.account.infrastructure.adapter.out.mapper;

import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.NumberAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.SoldeAccountStatisticProj;
import com.alibou.finance.shared.infrastructure.dto.NumberAccountStatistic;
import com.alibou.finance.shared.infrastructure.dto.SoldeAccountStatistic;

public class AccountStatisticMapper {
    public static NumberAccountStatistic toNumberAccountStatistic(NumberAccountStatisticProj projection){
        return new NumberAccountStatistic(
                projection.getAccountType(),
                projection.getNbrAccountByType()
        );
    }

    public static SoldeAccountStatistic toSoldAccountStatistic(SoldeAccountStatisticProj projection){
        return new SoldeAccountStatistic(
                projection.getAccountType(),
                projection.getSoldeAccountByType()
        );
    }
}
