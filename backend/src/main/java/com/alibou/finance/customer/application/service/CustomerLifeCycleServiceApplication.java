package com.alibou.finance.customer.application.service;

import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.customer.application.port.CustomerLifeCycleUseCase;
import com.alibou.finance.customer.domain.exception.CustomerNotFoundException;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.agregate.CustomerStatus;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerLifeCycleServiceApplication implements CustomerLifeCycleUseCase {

    private final CustomerRepository customerRepository;
    private final UserUseCase userUseCase;


    @Override
    public boolean verifyIfCustomerIsActive(CustomerId customerId) {
        Customer customer = getCustomerById(customerId);
        return customer.getStatus().value() == CustomerStatus.ACTIVE;
    }

    @Override
    public boolean closeCustomerAccount(CustomerId customerId) {
        Customer customer = getCustomerById(customerId);
        customer.close();
        customerRepository.closeAccount(customer);
        userUseCase.disableUser(customer.getUser().getUserId());
        return true;
    }

    @Override
    public CustomerStatus updateStatusCustomer(CustomerId customerId,  CustomerStatus status) {
        Customer customer = getCustomerById(customerId);

        if(status == CustomerStatus.ACTIVE){
            customer.active();
        } else if(status == CustomerStatus.SUSPENDED) {
            customer.suspend();
        } else {
            throw new OperationNotPermittedException("Modification status invalide");
        }
        return customerRepository.updateCustomerStatus(customer);
    }

    private Customer getCustomerById(CustomerId customerId){
        return customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: identifiant client invalide")
        );
    }
}
