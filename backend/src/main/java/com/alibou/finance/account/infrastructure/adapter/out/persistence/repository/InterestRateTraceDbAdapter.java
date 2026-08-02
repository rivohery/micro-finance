package com.alibou.finance.account.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.account.domain.agregate.InterestRateTrace;
import com.alibou.finance.account.domain.out.repository.InterestRateTraceRepository;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.InterestRateTraceMapper;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.InterestRateTraceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InterestRateTraceDbAdapter implements InterestRateTraceRepository {

    private final InterestRateTraceJpaRepository interestRateTraceJpaRepository;

    @Override
    public InterestRateTrace save(InterestRateTrace interestRateTrace) {
        InterestRateTraceEntity entity = InterestRateTraceMapper.toEntity(interestRateTrace);
        return InterestRateTraceMapper.toDomain(interestRateTraceJpaRepository.save(entity));
    }

    @Override
    public BigDecimal getTotalMonthlyInterestRate(String month, String year) {
        return interestRateTraceJpaRepository.getTotalMonthlyInterestRate(month, year);
    }

    @Override
    public Page<InterestRateTrace> findAllMonthlyInterestRateTrace(String month, String year, Pageable pageable) {
        return interestRateTraceJpaRepository.findAllByMonthEqualsAndYearEquals(month, year, pageable).map(InterestRateTraceMapper::toDomain);
    }
}
