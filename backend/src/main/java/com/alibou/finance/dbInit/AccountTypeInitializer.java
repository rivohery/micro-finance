package com.alibou.finance.dbInit;

import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.repository.AccountTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Profile({"dev"})
@Order(value = 2)
@RequiredArgsConstructor
@Component
public class AccountTypeInitializer implements CommandLineRunner {

    private final AccountTypeJpaRepository accountTypeJpaRepository;

    @Override
    public void run(String... args) throws Exception {
        AccountTypeEntity courante = AccountTypeEntity
                .builder()
                .accountFee(BigDecimal.valueOf(10))
                .annualInterestRate(BigDecimal.ZERO)
                .minimumBalance(BigDecimal.ZERO)
                .code("10")
                .id(UUID.randomUUID())
                .name("Compte Courante".toUpperCase())
                .build();
        AccountTypeEntity epargne = AccountTypeEntity
                .builder()
                .accountFee(BigDecimal.ZERO)
                .annualInterestRate(BigDecimal.valueOf(0.05))
                .minimumBalance(BigDecimal.valueOf(1000))
                .code("20")
                .id(UUID.randomUUID())
                .name("Compte Epargne".toUpperCase())
                .build();
        AccountTypeEntity business = AccountTypeEntity
                .builder()
                .accountFee(BigDecimal.valueOf(100.0))
                .annualInterestRate(BigDecimal.valueOf(0.09))
                .minimumBalance(BigDecimal.valueOf(1000))
                .code("30")
                .id(UUID.randomUUID())
                .name("Compte Business".toUpperCase())
                .build();
        List.of(courante, epargne, business).forEach(type -> {
            if(!accountTypeJpaRepository.existsByName(type.getName())){
                accountTypeJpaRepository.save(type);
            }
        });
    }
}
