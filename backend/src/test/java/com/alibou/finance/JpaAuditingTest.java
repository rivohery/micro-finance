package com.alibou.finance;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.repository.AccountTypeJpaRepository;
import com.alibou.finance.currency.infrastructure.adapter.out.entity.CurrencyEntity;
import com.alibou.finance.currency.infrastructure.adapter.out.repository.CurrencyJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingTestConfig.class)
public class JpaAuditingTest extends BaseRepositoryTest{
    @Autowired
    private CurrencyJpaRepository currencyJpaRepository;
    @Autowired
    private AccountJpaRepository accountJpaRepository;
    @Autowired
    private AccountTypeJpaRepository accountTypeJpaRepository;

    @BeforeEach
    void setUp() {
        accountJpaRepository.deleteAll();
        currencyJpaRepository.deleteAll();
        accountTypeJpaRepository.deleteAll();
    }

    //@Test
    void testJpaAuditingConfig(){
        CurrencyEntity usd = CurrencyEntity
                .builder()
                .name("Dollar")
                .id(UUID.randomUUID())
                .enable(true)
                .code("USD")
                .build();
        usd = currencyJpaRepository.save(usd);

        AccountTypeEntity savingAccountType = AccountTypeEntity.builder()
                .accountFee(BigDecimal.ZERO)
                .annualInterestRate(BigDecimal.valueOf(0.2))
                .code("20")
                .minimumBalance(BigDecimal.ZERO)
                .id(UUID.randomUUID())
                .name("compte épargne")
                .build();
        savingAccountType = accountTypeJpaRepository.save(savingAccountType);
        AccountEntity sourceAccount = AccountEntity.builder()
                .id(UUID.randomUUID())
                .accountNumber("001-10-1234567890")
                .accountStatus(AccountStatusEnum.ACTIVE)
                .accountTypeEntity(savingAccountType)
                .balance(new BigDecimal("100.00"))
                .mgaBalance(BigDecimal.valueOf(500000.00))
                .currencyEntity(usd)
                .customerId(UUID.randomUUID())
                .overdraftLimit(BigDecimal.ZERO)
                .build();
        sourceAccount = accountJpaRepository.save(sourceAccount);
        System.out.println("SourceAccount createdDate: " + sourceAccount.getCreatedDate());
        System.out.println("SourceAccount createdBy: " + sourceAccount.getCreatedBy());
        System.out.println("savingAccount createdDate: "+ savingAccountType.getCreatedDate());
        System.out.println("savingAccount createdBy: "+ savingAccountType.getCreatedBy());

        List<AccountEntity>accounts = accountJpaRepository.findAll();
        assertThat(accounts).extracting(a -> a.getCreatedDate()).containsOnly(
                LocalDateTime.of(2024, 1, 1, 12, 0, 0).toLocalDate()
        );
        assertThat(accounts).extracting(a -> a.getCreatedBy()).containsOnly(
                UUID.fromString("00000000-0000-0000-0000-000000000000")
        );

    }
}
