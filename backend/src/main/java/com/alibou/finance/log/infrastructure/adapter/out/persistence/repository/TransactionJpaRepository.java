package com.alibou.finance.log.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.log.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {
    Page<TransactionEntity> findAllByAccountNumber(String accountNumber, Pageable pageable);
    Optional<TransactionEntity>findByReference(String reference);
    Page<TransactionEntity>findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    List<TransactionEntity>findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
        select t from TransactionEntity t where t.accountNumber = :accountNumber
        and t.createdDate between :startMonth and :endMonth
        order by t.createdDate DESC 
    """)
    List<TransactionEntity>checkMonthlyTransactionOfOneAccount(
            @Param("accountNumber") String accountNumber,
            @Param("startMonth")LocalDateTime startMonth,
            @Param("endMonth")LocalDateTime endMonth
    );
}
