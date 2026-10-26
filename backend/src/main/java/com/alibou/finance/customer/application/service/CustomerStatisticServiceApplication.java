package com.alibou.finance.customer.application.service;

import com.alibou.finance.customer.application.usecase.CustomerStatisticUseCase;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.infrastructure.adapter.out.mapper.RegistrationStatisticMapper;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection.RegistrationStatisticProj;
import com.alibou.finance.shared.infrastructure.dto.RegistrationStatistic;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class CustomerStatisticServiceApplication implements CustomerStatisticUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public Long getNbrTotalOfCustomer() {
        return customerRepository.getNbrTotalOfCustomerNoClosed();
    }

    @Override
    public List<RegistrationStatistic> getDailyCustomerRegistrationOfWeek(LocalDate startWeek, LocalDate endWeek) {
        return customerRepository.getDailyCustomerRegistrationOfWeek(startWeek, endWeek)
                .stream()
                .sorted(Comparator.comparing(RegistrationStatisticProj::getCreatedDate))
                .map(RegistrationStatisticMapper::toRegistrationStatistic)
                .toList();
    }
}
