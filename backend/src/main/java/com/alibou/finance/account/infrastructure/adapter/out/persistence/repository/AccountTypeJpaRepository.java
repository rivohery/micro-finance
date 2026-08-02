package com.alibou.finance.account.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface AccountTypeJpaRepository extends JpaRepository<AccountTypeEntity, UUID> {
    Optional<AccountTypeEntity>findByCode(String code);
    boolean existsByName(String name);

}
