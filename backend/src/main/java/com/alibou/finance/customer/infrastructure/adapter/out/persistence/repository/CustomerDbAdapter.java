package com.alibou.finance.customer.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.customer.domain.vo.PhoneNumber;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.customer.domain.vo.Cin;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.customer.infrastructure.adapter.out.mapper.CustomerMapper;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection.RegistrationStatisticProj;
import com.alibou.finance.shared.application.result.PageResult;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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
    public Optional<CustomerId> findCustomerIdByUserId(UserId userId) {
        return customerJpaRepository.findIdByUserId(userId.value()).map(CustomerId::new);
    }

    @Override
    public PageResult<Customer> searchOperableCustomer(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerEntity> pagesEntity = customerJpaRepository.searchOperableCustomer(search, pageable);
        return PageMapper.toPageResult(pagesEntity, CustomerMapper::entityToDomain);
    }

    @Override
    public PageResult<Customer> fetchAllCustomerBySearchStart(String search,  int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerEntity> pages = customerJpaRepository.fetchAllCustomerBySearchStart(search, pageable);
        return PageMapper.toPageResult(pages, CustomerMapper::entityToDomain);
    }

    @Override
    public boolean existsByCin(Cin cin) {
        return customerJpaRepository.existsByCin(cin.value());
    }

    @Override
    public boolean existsByPhoneNumber(PhoneNumber phoneNumber) {
        return customerJpaRepository.existsByPhoneNumber(phoneNumber.value());
    }

    @Override
    public Optional<Customer> getCustomerByUserId(UserId userId) {
        return customerJpaRepository.findByUserId(userId.value()).map(CustomerMapper::entityToDomain);
    }

    @Override
    public boolean existsById(UUID customerId) {
        return customerJpaRepository.existsById(customerId);
    }

    @Override
    public Long getNbrTotalOfCustomerNoClosed() {
        return customerJpaRepository.getNbrTotalOfCustomerNoClosed();
    }

    @Override
    public List<RegistrationStatisticProj> getDailyCustomerRegistrationOfWeek(LocalDate startWeek, LocalDate endWeek) {
        return customerJpaRepository.getCustomersPerDayOfWeek(startWeek, endWeek);
    }
}
