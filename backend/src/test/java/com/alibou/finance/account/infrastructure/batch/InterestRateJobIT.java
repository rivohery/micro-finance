package com.alibou.finance.account.infrastructure.batch;

import com.alibou.finance.BaseServiceIT;
import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.repository.AccountTypeJpaRepository;
import com.alibou.finance.currency.infrastructure.adapter.out.entity.CurrencyEntity;
import com.alibou.finance.currency.infrastructure.adapter.out.repository.CurrencyJpaRepository;
import com.alibou.finance.history.infrastructure.adapter.out.persistence.entity.InterestRateTraceEntity;
import com.alibou.finance.history.infrastructure.adapter.out.persistence.repository.InterestRateTraceJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
public class InterestRateJobIT extends BaseServiceIT {
    @Autowired
    private AccountTypeJpaRepository accountTypeJpaRepository;
    @Autowired
    private CurrencyJpaRepository currencyJpaRepository;
    @Autowired
    private AccountJpaRepository accountJpaRepository;
    @Autowired
    private InterestRateTraceJpaRepository interestRateTraceJpaRepository;
    @Autowired
    private JobLauncher jobLauncher;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private CurrencyExchangePort currencyExchangePort;


    @Autowired
    @Qualifier("jobCalculInteretFinDeMois")
    private Job job;

    AccountTypeEntity checkingAccount;
    AccountTypeEntity savingAccount;
    AccountTypeEntity businessAccount;
    CurrencyEntity eurCurrency;
    CurrencyEntity mgaCurrency;
    CurrencyEntity usdCurrency;
    JobParameters params;

    @BeforeEach
    void setUp(){
        checkingAccount = createMinType("10", "Compte courante");
        savingAccount = createMinType("20", "Compte épargne");
        businessAccount = createMinType("30", "Compte business");
        accountTypeJpaRepository.saveAllAndFlush(Set.of(checkingAccount, savingAccount, businessAccount));

        eurCurrency = createMinCurrency("EUR", "Euro");
        mgaCurrency = createMinCurrency("MGA", "Ariary");
        usdCurrency = createMinCurrency("USD", "Dollar");
        currencyJpaRepository.saveAllAndFlush(Set.of(eurCurrency, mgaCurrency, usdCurrency));

        params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
    }

    @Test
    void shouldExecuteInterestRateJobCorrectly()
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException
    {
        //Given
        AccountEntity acc1 = createMinAccount(savingAccount, mgaCurrency, "101-20-6123456789", AccountStatusEnum.ACTIVE);
        AccountEntity acc2 = createMinAccount(checkingAccount, eurCurrency, "101-10-6123456789", AccountStatusEnum.ACTIVE);
        AccountEntity acc3 = createMinAccount(savingAccount, mgaCurrency, "101-20-6723456789", AccountStatusEnum.SUSPENDED);
        AccountEntity acc4 = createMinAccount(businessAccount, eurCurrency, "101-30-6123456789", AccountStatusEnum.ACTIVE);
        AccountEntity acc5 = createMinAccount(checkingAccount, usdCurrency, "101-10-6123456780", AccountStatusEnum.ACTIVE);
        AccountEntity acc6 = createMinAccount(savingAccount, usdCurrency, "101-30-6123456000", AccountStatusEnum.ACTIVE);
        accountJpaRepository.saveAllAndFlush(Set.of(acc1, acc2, acc3, acc4, acc5, acc6));

        when(transactionRepository.checkMonthlyTransactionOfAccount(
                any(AccountNumber.class), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList()); //pour faire simple


        BigDecimal eurExchangeRate = new BigDecimal("4500.0");
        BigDecimal usdExchangeRate = new BigDecimal("4000.0");
        BigDecimal mgaExchangeRate = BigDecimal.ONE;
        when(currencyExchangePort.getExchangeRate("USD", "MGA")).thenReturn(usdExchangeRate);
        when(currencyExchangePort.getExchangeRate("EUR", "MGA")).thenReturn(eurExchangeRate);
        when(currencyExchangePort.getExchangeRate("MGA", "MGA")).thenReturn(mgaExchangeRate);

        //When
        System.out.println("Lancement de Job...");
        JobExecution execution = jobLauncher.run(job, params);

        //Then
        if(execution.getStatus() != BatchStatus.COMPLETED){
            execution.getAllFailureExceptions().forEach(t -> System.out.println(t.getMessage()));
        }
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        StepExecution stepExec = execution.getStepExecutions().iterator().next();
        assertThat(stepExec.getReadCount()).isEqualTo(3);   // nb comptes lus
        assertThat(stepExec.getWriteCount()).isEqualTo(3);  // nb comptes écrits
        assertThat(stepExec.getCommitCount()).isEqualTo(1); // 3 comptes < chunkSize 100 → 1 commit
        assertThat(stepExec.getRollbackCount()).isZero();

        List<InterestRateTraceEntity> interestRateTraces = interestRateTraceJpaRepository.findAll();
        assertThat(interestRateTraces.size()).isEqualTo(3);

    }

    @Test
    void should_testPaginationRunning() throws Exception{
        List<AccountEntity> accounts = IntStream.range(0, 250)
                .mapToObj(i -> createMinAccount(savingAccount, mgaCurrency,
                        "101-20-%010d".formatted(i), AccountStatusEnum.ACTIVE))
                .toList();
        accountJpaRepository.saveAllAndFlush(accounts);
        when(currencyExchangePort.getExchangeRate("MGA", "MGA")).thenReturn(BigDecimal.ONE);

        System.out.println("Lancement de Job...");
        JobExecution execution = jobLauncher.run(job, params);

        //Then
        if(execution.getStatus() != BatchStatus.COMPLETED){
            execution.getAllFailureExceptions().forEach(t -> System.out.println(t.getMessage()));
        }
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        StepExecution stepExec = execution.getStepExecutions().iterator().next();
        assertThat(stepExec.getReadCount()).isEqualTo(250);
        assertThat(stepExec.getWriteCount()).isEqualTo(250);
        // 250 comptes / 100 par page → 3 commits (100 + 100 + 50)
        assertThat(stepExec.getCommitCount()).isEqualTo(3);

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
        BigDecimal annualInterestRate;
        if(code.equals("20")){
            annualInterestRate = new BigDecimal("3.65");
        } else {
            annualInterestRate = new BigDecimal("7.30");
        }
        return AccountTypeEntity.builder()
                .accountFee(BigDecimal.ZERO)
                .id(UUID.randomUUID())
                .annualInterestRate(annualInterestRate)
                .minimumBalance(BigDecimal.ZERO)
                .code(code)
                .name(name)
                .createdBy(UUID.randomUUID())
                .build();
    }
}
