package com.alibou.finance.customer.domain.out.repository;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.agregate.CustomerStatus;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.application.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);

    Optional<Customer>findById(CustomerId customerId);

    Optional<CustomerId> findCustomerIdByUser(User user);

    boolean existsByCin(String cin);

    PageResult<Customer> fetchAllEnableCustomerBySearchBegin(String search, int page, int size);

    PageResult<Customer>findAllCustomerBySearchBegin(String search,  int page, int size);

    CustomerStatus updateCustomerStatus(Customer customer);

    void closeAccount(Customer customer);

    boolean existsById(UUID customerId);

}
