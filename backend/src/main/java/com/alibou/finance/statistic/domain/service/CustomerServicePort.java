package com.alibou.finance.statistic.domain.service;

import com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection.RegistrationStatisticProj;

import java.time.LocalDate;
import java.util.List;

public interface CustomerServicePort {
    Long getNbrTotalOfCustomer();
    List<RegistrationStatisticProj> getRegistrationStatisticsByWeek(LocalDate startWeek, LocalDate endWeek);
}
