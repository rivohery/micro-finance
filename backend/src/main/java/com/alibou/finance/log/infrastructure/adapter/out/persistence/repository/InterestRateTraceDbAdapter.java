package com.alibou.finance.log.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.log.domain.agregate.InterestRateTrace;
import com.alibou.finance.log.domain.repository.InterestRateTraceRepository;
import com.alibou.finance.log.infrastructure.adapter.out.mappers.InterestRateTraceMapper;
import com.alibou.finance.log.infrastructure.adapter.out.persistence.entity.InterestRateTraceEntity;
import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InterestRateTraceDbAdapter implements InterestRateTraceRepository {

    private final InterestRateTraceJpaRepository interestRateTraceJpaRepository;

    @Override
    public InterestRateTrace save(InterestRateTrace interestRateTrace) {
        InterestRateTraceEntity entity = InterestRateTraceMapper.domainToEntity(interestRateTrace);
        return InterestRateTraceMapper.entityToDomain(interestRateTraceJpaRepository.save(entity));
    }

    @Override
    public BigDecimal getTotalMonthlyInterestRate(String month, String year) {
        return interestRateTraceJpaRepository.getTotalMonthlyInterestRate(month, year);
    }

    @Override
    public PageResult<InterestRateTrace> findAllMonthlyInterestRateTrace(String month, String year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InterestRateTraceEntity>pageEntities = interestRateTraceJpaRepository.findAllByMonthEqualsAndYearEquals(month, year, pageable);
        return PageMapper.toPageResult(pageEntities, InterestRateTraceMapper::entityToDomain);
    }
}
