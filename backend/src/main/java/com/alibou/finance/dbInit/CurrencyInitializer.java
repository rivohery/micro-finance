package com.alibou.finance.dbInit;

import com.alibou.finance.currency.infrastructure.adapter.out.entity.CurrencyEntity;
import com.alibou.finance.currency.infrastructure.adapter.out.repository.CurrencyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


@Profile({"dev"})
@Order(value = 3)
@RequiredArgsConstructor
@Component
public class CurrencyInitializer implements CommandLineRunner{
    private final CurrencyJpaRepository currencyJpaRepository;

    @Override
    public void run(String... args) throws Exception {
        CurrencyEntity mgaCurrency = CurrencyEntity.builder()
                .id(UUID.randomUUID())
                .code("MGA")
                .name("Ariary")
                .enable(true)
                .build();
        CurrencyEntity usdCurrency = CurrencyEntity.builder()
                .id(UUID.randomUUID())
                .code("USD")
                .name("Dollar USA")
                .enable(true)
                .build();
        CurrencyEntity eurCurrency = CurrencyEntity.builder()
                .id(UUID.randomUUID())
                .code("EUR")
                .name("EURO")
                .enable(true)
                .build();
        List.of(mgaCurrency, usdCurrency, eurCurrency).forEach(curr -> {
            if(!currencyJpaRepository.existsByCode(curr.getCode())){
                currencyJpaRepository.save(curr);
            }
        });
    }
}