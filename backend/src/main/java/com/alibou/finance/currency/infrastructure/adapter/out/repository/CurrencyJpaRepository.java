package com.alibou.finance.currency.infrastructure.adapter.out.repository;

import com.alibou.finance.currency.infrastructure.adapter.out.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CurrencyJpaRepository extends JpaRepository<CurrencyEntity, UUID> {
    Optional<CurrencyEntity>findByCode(String code);
    List<CurrencyEntity>findAllByEnableIsTrue();

    boolean existsByCode(String code);
}
