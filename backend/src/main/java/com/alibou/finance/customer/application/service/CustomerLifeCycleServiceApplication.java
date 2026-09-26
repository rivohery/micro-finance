package com.alibou.finance.customer.application.service;

import com.alibou.finance.customer.application.usecase.CustomerLifeCycleUseCase;
import com.alibou.finance.customer.domain.exception.CustomerNotFoundException;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.agregate.CustomerStatusEnum;
import com.alibou.finance.customer.domain.out.linker.UserLinkerPort;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerLifeCycleServiceApplication implements CustomerLifeCycleUseCase {

    private final CustomerRepository customerRepository;
    private final UserLinkerPort userLinkerPort;


    @Override
    public boolean verifyIfCustomerIsActive(CustomerId customerId) {
        Customer customer = getCustomerById(customerId);
        return customer.getCustomerStatus().value() == CustomerStatusEnum.ACTIVE;
    }

    @Override
    public void closeCustomer(CustomerId customerId) {
        Customer customer = getCustomerById(customerId);
        customer.close();
        userLinkerPort.disableUser(customer.getUserId());
        customerRepository.save(customer);
    }

    @Override
    public void activeCustomer(CustomerId customerId) {
        Customer customer = getCustomerById(customerId);
        customer.active();
        customerRepository.save(customer);
    }

    @Override
    public void suspendCustomer(CustomerId customerId) {
        Customer customer = getCustomerById(customerId);
        customer.suspend();
        customerRepository.save(customer);
    }

    private Customer getCustomerById(CustomerId customerId){
        return customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: identifiant client invalide")
        );
    }
}
