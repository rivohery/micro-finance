package com.alibou.finance.customer.domain.out.repository;

import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.customer.domain.model.Customer;
import com.alibou.finance.customer.domain.model.CustomerStatus;
import com.alibou.finance.customer.domain.vo.CustomerId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);

    Optional<Customer>findById(CustomerId customerId);

    Optional<CustomerId> findCustomerIdByUser(User user);

    boolean existsByCin(String cin);

    Page<Customer>fetchAllEnableCustomerBySearchBegin(String search, Pageable pageable);

    Page<Customer>findAllCustomerBySearchBegin(String search, Pageable pageable);

    CustomerStatus updateCustomerStatus(Customer customer);

    void closeAccount(Customer customer);

    boolean existsById(UUID customerId);

}
