package com.alibou.finance.customer.infrastructure.transactional;

import com.alibou.finance.customer.application.port.UpdateCustomerUseCase;
import com.alibou.finance.customer.domain.agregate.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCustomerUseCaseProxy {

    private final UpdateCustomerUseCase updateCustomerUseCase;

    @Transactional
    public Customer execute(Customer customer, byte[] contentFile, String fileName) {
        return updateCustomerUseCase.execute(customer, contentFile, fileName);
    }

}
