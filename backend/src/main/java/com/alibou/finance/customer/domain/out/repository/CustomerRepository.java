package com.alibou.finance.customer.domain.out.repository;

import com.alibou.finance.customer.domain.vo.PhoneNumber;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.vo.Cin;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection.RegistrationStatisticProj;
import com.alibou.finance.shared.application.result.PageResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);

    Optional<Customer>findById(CustomerId customerId);

    Optional<Customer>getCustomerByUserId(UserId userId);

    Optional<CustomerId> findCustomerIdByUserId(UserId userId);

    boolean existsByCin(Cin cin);
    boolean existsByPhoneNumber(PhoneNumber phoneNumber);

    PageResult<Customer> searchOperableCustomer(String search, int page, int size);

    PageResult<Customer>fetchAllCustomerBySearchStart(String search,  int page, int size);

    boolean existsById(UUID customerId);

    Long getNbrTotalOfCustomerNoClosed();
    List<RegistrationStatisticProj> getDailyCustomerRegistrationOfWeek(LocalDate startWeek, LocalDate endWeek);

}
