package com.alibou.finance.customer.application.usecase;

import com.alibou.finance.shared.infrastructure.dto.RegistrationStatistic;

import java.time.LocalDate;
import java.util.List;

public interface CustomerStatisticUseCase {
    Long getNbrTotalOfCustomer();
    List<RegistrationStatistic> getDailyCustomerRegistrationOfWeek(
            LocalDate startWeek, LocalDate endWeek
    );
}
