package com.alibou.finance.account.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.InterestRateTraceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface InterestRateTraceJpaRepository extends JpaRepository<InterestRateTraceEntity, UUID> {

    @Query("""
        select sum(ir.mgaAmount) from InterestRateTraceEntity ir where ir.month =:month and ir.year =:year
    """)
    BigDecimal getTotalMonthlyInterestRate(@Param("month") String month,@Param("year") String year);

    Page<InterestRateTraceEntity>findAllByMonthEqualsAndYearEquals(String month, String year, Pageable pageable);
}
