package com.alibou.finance.accountType.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountTypeJpaRepository extends JpaRepository<AccountTypeEntity, UUID> {
    Optional<AccountTypeEntity>findByCode(String code);
    boolean existsByName(String name);

}
