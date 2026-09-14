package com.alibou.finance.customer.infrastructure.transactional;

import com.alibou.finance.customer.application.port.CustomerLifeCycleUseCase;
import com.alibou.finance.customer.domain.agregate.CustomerStatus;
import com.alibou.finance.customer.domain.vo.CustomerId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerLifeCycleUseCaseProxy {

    private final CustomerLifeCycleUseCase customerLifeCycleUseCase;

    @Transactional(readOnly = true)
    public boolean verifyIfCustomerIsActive(CustomerId customerId) {
        return customerLifeCycleUseCase.verifyIfCustomerIsActive(customerId);
    }

    @Transactional
    public CustomerStatus updateStatusCustomer(CustomerId customerId, CustomerStatus status) {
        return customerLifeCycleUseCase.updateStatusCustomer(customerId, status);
    }

    @Transactional
    public boolean closeCustomerAccount(CustomerId customerId) {
        return customerLifeCycleUseCase.closeCustomerAccount(customerId);
    }

}
