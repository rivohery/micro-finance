package com.alibou.finance.customer.infrastructure.transactional;

import com.alibou.finance.customer.application.usecase.CustomerLifeCycleUseCase;
import com.alibou.finance.customer.domain.agregate.CustomerStatusEnum;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
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
    public void updateStatusCustomer(CustomerId customerId, CustomerStatusEnum status) {
        if(status == CustomerStatusEnum.ACTIVE){
            customerLifeCycleUseCase.activeCustomer(customerId);
        }
        if(status == CustomerStatusEnum.SUSPENDED){
            customerLifeCycleUseCase.suspendCustomer(customerId);
        }
    }

    @Transactional
    public void closeCustomer(CustomerId customerId) {
        customerLifeCycleUseCase.closeCustomer(customerId);
    }

}
