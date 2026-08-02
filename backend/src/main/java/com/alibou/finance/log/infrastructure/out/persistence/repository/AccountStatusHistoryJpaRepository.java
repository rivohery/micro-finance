package com.alibou.finance.log.infrastructure.out.persistence.repository;

import com.alibou.finance.log.infrastructure.out.persistence.entity.AccountStatusHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountStatusHistoryJpaRepository extends JpaRepository<AccountStatusHistoryEntity, UUID> {
    Page<AccountStatusHistoryEntity>findAllByAccountId(UUID accountId, Pageable pageable);
}
