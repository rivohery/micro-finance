package com.alibou.finance.statistic.infrastructure.adapter.mapper;

import com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection.RegistrationStatisticProj;
import com.alibou.finance.statistic.domain.agregate.RegistrationStatistic;

public class RegistrationStatisticMapper {
    public static RegistrationStatistic fromProjection(RegistrationStatisticProj projection){
        return RegistrationStatistic.builder()
                    .dayOfWeek(projection.getCreatedDate().getDayOfWeek().name().toLowerCase())
                    .nbrCustomer(projection.getNbrCustomer())
                    .build();
    }
}
