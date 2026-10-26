package com.alibou.finance.customer.infrastructure.adapter.out.mapper;

import com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection.RegistrationStatisticProj;
import com.alibou.finance.shared.infrastructure.dto.RegistrationStatistic;

public class RegistrationStatisticMapper {
    public static RegistrationStatistic toRegistrationStatistic(RegistrationStatisticProj projection){
        return new RegistrationStatistic(
                projection.getCreatedDate().getDayOfWeek().name().toLowerCase(),
                projection.getNbrCustomer()
        );
    }
}
