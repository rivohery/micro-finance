package com.alibou.finance.statistic.infrastructure.adapter.out;

import com.alibou.finance.customer.domain.model.CustomerStatus;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection.RegistrationStatisticProj;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.repository.CustomerJpaRepository;
import com.alibou.finance.statistic.domain.service.CustomerServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerServicePortAdapter implements CustomerServicePort {

    private final CustomerJpaRepository customerJpaRepository;
    @Override
    public Long getNbrTotalOfCustomer() {
        return customerJpaRepository.getNbrTotalOfCustomerNoClosed(CustomerStatus.CLOSED);
    }

    @Override
    public List<RegistrationStatisticProj> getRegistrationStatisticsByWeek(LocalDate startWeek, LocalDate endWeek) {
        return customerJpaRepository.getCustomersPerDayOfWeek(startWeek, endWeek);
    }
}
