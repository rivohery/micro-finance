package com.alibou.finance.customer.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.customer.domain.agregate.CustomerStatus;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.customer.infrastructure.adapter.out.mapper.CustomerMapper;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerDbAdapter implements CustomerRepository {
    private final CustomerJpaRepository customerJpaRepository;

    @Override
    public Customer save(Customer customer) {
        var customerEntity = CustomerMapper.domainToEntity(customer);
        return CustomerMapper.entityToDomain(customerJpaRepository.save(customerEntity));
    }

    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        return customerJpaRepository.findById(customerId.value()).map(CustomerMapper::entityToDomain);
    }

    @Override
    public Optional<CustomerId> findCustomerIdByUser(User user) {
        return customerJpaRepository.findIdByUserId(user.getUserId().value()).map(CustomerId::new);
    }

    @Override
    public PageResult<Customer> fetchAllEnableCustomerBySearchBegin(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerEntity> pagesEntity = customerJpaRepository.fetchAllEnableCustomerBySearchBegin(search, pageable);
        return PageMapper.toPageResult(pagesEntity, CustomerMapper::entityToDomain);
    }

    @Override
    public PageResult<Customer> findAllCustomerBySearchBegin(String search,  int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerEntity> pages = customerJpaRepository.findAllByFirstNameStartingWithIgnoreCaseOrLastNameStartingWithIgnoreCaseOrCinStartingWith(
                        search, search, search, pageable
        );
        return PageMapper.toPageResult(pages, CustomerMapper::entityToDomain);
    }

    @Override
    public boolean existsByCin(String cin) {
        return customerJpaRepository.existsByCin(cin);
    }

    @Override
    public CustomerStatus updateCustomerStatus(Customer customer) {
        var customerEntity = CustomerMapper.domainToEntity(customer);
        return customerJpaRepository.save(customerEntity).getStatus();
    }

    @Override
    public void closeAccount(Customer customer) {
        customerJpaRepository.updateCustomerStatus(customer.getCustomerId().value(), customer.getStatus().value());
    }

    @Override
    public boolean existsById(UUID customerId) {
        return customerJpaRepository.existsById(customerId);
    }
}
