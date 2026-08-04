package com.alibou.finance.customer.infrastructure.transactional;

import com.alibou.finance.customer.application.port.CreateCustomerUseCase;
import com.alibou.finance.customer.domain.agregate.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCustomerUseCaseProxy {
    private final CreateCustomerUseCase createCustomerUseCase;

    @Transactional
    public Customer execute(Customer customer, byte[] contentFile, String fileName){
        return createCustomerUseCase.execute(customer, contentFile, fileName);
    }
}
