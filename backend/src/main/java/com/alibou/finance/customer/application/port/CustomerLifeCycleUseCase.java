package com.alibou.finance.customer.application.port;

import com.alibou.finance.customer.domain.agregate.CustomerStatus;
import com.alibou.finance.customer.domain.vo.CustomerId;

public interface CustomerLifeCycleUseCase {
    boolean verifyIfCustomerIsActive(CustomerId customerId);
    CustomerStatus updateStatusCustomer(CustomerId customerId, CustomerStatus status);

    /**
     * Clôture définitive du compte client en le désactivant totalement. Action irreversible
     */
    boolean closeCustomerAccount(CustomerId customerId);
}
