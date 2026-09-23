package com.alibou.finance.account.infrastructure.batch;

import com.alibou.finance.BaseServiceIT;
import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.repository.AccountTypeJpaRepository;
import com.alibou.finance.currency.infrastructure.adapter.out.entity.CurrencyEntity;
import com.alibou.finance.currency.infrastructure.adapter.out.repository.CurrencyJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

public class CalculMgaBalanceJobIT extends BaseServiceIT {
    @Autowired
    private AccountTypeJpaRepository accountTypeJpaRepository;
    @Autowired
    private CurrencyJpaRepository currencyJpaRepository;
    @Autowired
    private AccountJpaRepository accountJpaRepository;
    @MockBean
    private CurrencyExchangePort currencyExchangePort;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    @Qualifier("jobCalculMgaBalanceFinDeSemaine")
    private Job  job;

    @BeforeEach
    void setUp(){
        AccountTypeEntity checkingAccount = createMinType("10", "Compte courante");
        AccountTypeEntity savingAccount = createMinType("20", "Compte épargne");
        AccountTypeEntity businessAccount = createMinType("30", "Compte business");
        accountTypeJpaRepository.saveAllAndFlush(Set.of(checkingAccount, savingAccount, businessAccount));

        CurrencyEntity eurCurrency = createMinCurrency("EUR", "Euro");
        CurrencyEntity mgaCurrency = createMinCurrency("MGA", "Ariary");
        CurrencyEntity usdCurrency = createMinCurrency("USD", "Dollar");
        currencyJpaRepository.saveAllAndFlush(Set.of(eurCurrency, mgaCurrency, usdCurrency));

        AccountEntity acc1 = createMinAccount(savingAccount, mgaCurrency, "101-20-6123456789", AccountStatusEnum.CLOSED);
        AccountEntity acc2 = createMinAccount(checkingAccount, eurCurrency, "101-10-6123456789", AccountStatusEnum.ACTIVE);
        AccountEntity acc3 = createMinAccount(savingAccount, eurCurrency, "101-20-6723456789", AccountStatusEnum.SUSPENDED);
        AccountEntity acc4 = createMinAccount(businessAccount, mgaCurrency, "101-30-6123456789", AccountStatusEnum.PENDING); //Solde Zero
        AccountEntity acc5 = createMinAccount(checkingAccount, usdCurrency, "101-10-6123456780", AccountStatusEnum.ACTIVE);
        accountJpaRepository.saveAllAndFlush(Set.of(acc1, acc2, acc3, acc4, acc5));

    }

    @Test
    void shouldCalculMgaBalanceSuccessfully() throws Exception{
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        BigDecimal eurExchangeRate = new BigDecimal("4500.0");
        BigDecimal usdExchangeRate = new BigDecimal("4000.0");
        when(currencyExchangePort.getExchangeRate("USD", "MGA")).thenReturn(usdExchangeRate);
        when(currencyExchangePort.getExchangeRate("EUR", "MGA")).thenReturn(eurExchangeRate);


        System.out.println("Lancement de Job...");

        JobExecution execution = jobLauncher.run(job, params);
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        List<AccountEntity> accounts = accountJpaRepository.findAll();
        assertThat(accounts).extracting(a -> a.getMgaBalance().compareTo(BigDecimal.ZERO)).containsExactlyInAnyOrder(0,1,1,0,1);
        assertThat(accounts).extracting(a -> a.getMgaBalance().doubleValue()).containsExactlyInAnyOrder(0.0, 9000000.0, 0.0, 9000000.0, 8000000.0);
    }

    private AccountEntity createMinAccount(AccountTypeEntity accountType, CurrencyEntity currency, String accountNumber, AccountStatusEnum status){
        return AccountEntity.builder()
                .accountNumber(accountNumber)
                .id(UUID.randomUUID())
                .accountStatus(status)
                .balance(new BigDecimal("2000.0"))
                .mgaBalance(BigDecimal.ZERO)
                .accountTypeEntity(accountType)
                .currencyEntity(currency)
                .customerId(UUID.randomUUID())
                .createdBy(UUID.randomUUID())
                .overdraftLimit(BigDecimal.ZERO)
                .version(1L)
                .build();
    }

    private CurrencyEntity createMinCurrency(String code, String name){
        return CurrencyEntity.builder()
                .id(UUID.randomUUID())
                .code(code)
                .enable(true)
                .name(name)
                .build();
    }

    private AccountTypeEntity createMinType(String code, String name){
        return AccountTypeEntity.builder()
                .accountFee(BigDecimal.ZERO)
                .id(UUID.randomUUID())
                .annualInterestRate(new BigDecimal("0.0001"))
                .minimumBalance(BigDecimal.ZERO)
                .code(code)
                .name(name)
                .createdBy(UUID.randomUUID())
                .build();
    }
}
