package com.alibou.finance.account.infrastructure.batch;

import com.alibou.finance.BaseServiceIT;
import com.alibou.finance.account.domain.out.linker.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.repository.AccountJpaRepository;
import com.alibou.finance.history.infrastructure.adapter.out.persistence.entity.InterestRateTraceEntity;
import com.alibou.finance.history.infrastructure.adapter.out.persistence.repository.InterestRateTraceJpaRepository;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
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
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
public class InterestRateJobIT extends BaseServiceIT {

    @Autowired
    private AccountJpaRepository accountJpaRepository;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private InterestRateTraceJpaRepository interestRateTraceJpaRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private CurrencyExchangePort currencyExchangePort;


    @Autowired
    @Qualifier("jobCalculInteretFinDeMois")
    private Job job;


    CurrencyCode eurCurrency = new CurrencyCode("EUR");
    CurrencyCode mgaCurrency = new CurrencyCode("MGA");
    CurrencyCode usdCurrency = new CurrencyCode("USD");
    JobParameters params;

    @BeforeEach
    void setUp(){
        accountJpaRepository.deleteAll();
        interestRateTraceJpaRepository.deleteAll();

        params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
    }

    @Test
    void shouldExecuteInterestRateJobCorrectly()
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException
    {
        //Given
        AccountEntity acc1 = createMinAccount("20", "compte épargne", mgaCurrency, "101-20-6123456789", AccountStatusEnum.ACTIVE);
        AccountEntity acc2 = createMinAccount("10", "compte courant", eurCurrency, "101-10-6123456789", AccountStatusEnum.ACTIVE);
        AccountEntity acc3 = createMinAccount("20", "compte épargne", mgaCurrency, "101-20-6723456789", AccountStatusEnum.SUSPENDED);
        AccountEntity acc4 = createMinAccount("30", "compte business", eurCurrency, "101-30-6123456789", AccountStatusEnum.ACTIVE);
        AccountEntity acc5 = createMinAccount("10", "compte courant", usdCurrency, "101-10-6123456780", AccountStatusEnum.ACTIVE);
        AccountEntity acc6 = createMinAccount("20", "compte épargne", usdCurrency, "101-30-6123456000", AccountStatusEnum.ACTIVE);
        accountJpaRepository.saveAllAndFlush(Set.of(acc1, acc2, acc3, acc4, acc5, acc6));

        when(transactionRepository.findMonthlyTransactionsOfAccount(
                any(AccountNumber.class), any(YearMonth.class)
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
                .mapToObj(i -> createMinAccount("20", "compte épargne", mgaCurrency,
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
        assertThat(stepExec.getRollbackCount()).isZero();

    }

    private AccountEntity createMinAccount(String accountTypeCode, String accountTypeName, CurrencyCode currencyCode, String accountNumber, AccountStatusEnum status){
        return AccountEntity.builder()
                .accountNumber(accountNumber)
                .id(UUID.randomUUID())
                .accountStatus(status)
                .balance(new BigDecimal("2000.0"))
                .mgaBalance(BigDecimal.ZERO)
                .accountTypeId(UUID.randomUUID())
                .accountTypeName(accountTypeName)
                .accountTypeCode(accountTypeCode)
                .annualInterestRate(new BigDecimal("3.65"))
                .currencyCode(currencyCode.value())
                .currencyId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .createdBy(UUID.randomUUID())
                .overdraftLimit(BigDecimal.ZERO)
                .version(1L)
                .build();
    }



}
