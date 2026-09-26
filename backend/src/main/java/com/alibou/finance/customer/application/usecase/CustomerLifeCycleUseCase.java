package com.alibou.finance.customer.application.usecase;

import com.alibou.finance.shared.domain.vo.user.CustomerId;

public interface CustomerLifeCycleUseCase {
    boolean verifyIfCustomerIsActive(CustomerId customerId);

    void activeCustomer(CustomerId customerId);

    void suspendCustomer(CustomerId customerId);

    void closeCustomer(CustomerId customerId);
}
