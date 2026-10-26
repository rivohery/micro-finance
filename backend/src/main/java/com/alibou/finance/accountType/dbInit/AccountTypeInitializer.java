package com.alibou.finance.accountType.dbInit;

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
        AccountTypeEntity courante = prepare("0.00", "1000.0", "10", "compte courante");
        AccountTypeEntity epargne = prepare("3.65", "1000.0", "20", "compte epargne");;
        AccountTypeEntity business = prepare("7.30", "1000.0", "30", "compte business");
        List.of(courante, epargne, business).forEach(type -> {
            if(!accountTypeJpaRepository.existsByName(type.getName())){
                accountTypeJpaRepository.save(type);
            }
        });
    }

    private AccountTypeEntity prepare(String annualInterestRate, String minimumBalance, String code, String name){
        return AccountTypeEntity
                .builder()
                .accountFee(BigDecimal.ZERO)
                .annualInterestRate(new BigDecimal(annualInterestRate))
                .minimumBalance(new BigDecimal(minimumBalance))
                .code(code)
                .id(UUID.randomUUID())
                .name(name.toUpperCase())
                .createdBy(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .build();
    }
}
