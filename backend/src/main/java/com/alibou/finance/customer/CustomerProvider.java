package com.alibou.finance.customer;

import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.infrastructure.dto.RegistrationStatistic;

import java.time.LocalDate;
import java.util.List;

public interface CustomerProvider {
    List<RegistrationStatistic> getRegistrationStatisticsByWeek(LocalDate startWeek, LocalDate endWeek);
    Long getNbrTotalOfCustomer();
    CustomerId findCustomerIdByUserId(UserId userId);
    boolean verifyIfCustomerIsActive(CustomerId customerId);
}
