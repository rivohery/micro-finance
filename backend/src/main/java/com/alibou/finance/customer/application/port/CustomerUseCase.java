package com.alibou.finance.customer.application.port;

import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.customer.domain.model.Customer;
import com.alibou.finance.customer.domain.model.CustomerStatus;
import com.alibou.finance.customer.domain.vo.CustomerId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerUseCase {
    Customer create(Customer customer, byte[] contentFile, String fileName);

    Customer update(Customer customer, byte[] contentFile, String fileName);

    Customer findCustomerDetailsById(CustomerId customerId);
    Page<Customer> findAllEnableCustomerBySearch(String search, Pageable pageable);

    Page<Customer> findAllCustomerBySearchStart(String search, Pageable pageable);

    CustomerId findCustomerIdByUser(User user);

    boolean verifyIfCustomerIsActive(CustomerId customerId);
    CustomerStatus updateStatusCustomer(CustomerId customerId,  CustomerStatus status);

    /**
     * Clôture définitive du compte client en le désactivant totalement. Action irreversible
     */
    boolean CloseCustomerAccount(CustomerId customerId);

    DetailCustomerWithAccount findCustomerWithAccounts(CustomerId customerId);

}
