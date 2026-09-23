package com.alibou.finance.account.infrastructure.batch.config;

import com.alibou.finance.account.application.port.usecase.CalculMgaBalanceUseCase;
import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.batch.processor.CalculMgaBalanceProcessor;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class CalculMgaBalanceBatchConfig {

    /**
     * READER : mono-thread, donc JpaPagingItemReader est sûr.
     * - pageSize=100 : lit par paquets de 100 comptes
     * - transacted=false : le reader utilise la transaction du chunk (évite "Transaction already active")
     * - saveState=true : sauvegarde la position de lecture pour reprise sur incident
     */
    @Bean("mgaBalanceReader")
    public JpaPagingItemReader<AccountEntity> mgaBalanceReader(EntityManagerFactory entityManagerFactory) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("status", Set.of(AccountStatusEnum.CLOSED, AccountStatusEnum.PENDING));

        return new JpaPagingItemReaderBuilder<AccountEntity>()
                .name("mgaBalanceReader")
                .entityManagerFactory(entityManagerFactory)
                // 2. Requête JPQL directe avec un tri STRICTEMENT UNIQUE (id)
                .queryString("SELECT a FROM AccountEntity a " +
                        "JOIN FETCH a.currencyEntity c " +
                        "JOIN FETCH a.accountTypeEntity at " +
                        "WHERE a.accountStatus NOT IN :status " +
                        "ORDER BY a.id ASC")
                .parameterValues(parameters)
                .pageSize(100)
                .transacted(false)
                .saveState(true)
                .build();
    }

    @Bean("mgaBalanceProcessor")
    public ItemProcessor<AccountEntity, AccountEntity> mgaBalanceProcessor(CalculMgaBalanceUseCase calculMgaBalanceUseCase) {
        return new CalculMgaBalanceProcessor(calculMgaBalanceUseCase);
    }

    /**
     * WRITER : sauvegarde les comptes modifiés en base (JPA).
     * Il utilise l'EntityManager de la transaction du chunk.
     */
    @Bean("mgaBalanceWriter")
    public JpaItemWriter<AccountEntity> mgaBalanceWriter(EntityManagerFactory emf) {
        return new JpaItemWriterBuilder<AccountEntity>()
                .entityManagerFactory(emf)
                .build();
    }

    /**
     * STEP : chunk-oriented classique.
     * - chunk(100, transactionManager) : 1 transaction par paquet de 100 comptes
     * - pas de TaskExecutor : mono-thread
     * - retry sur DataAccessResourceFailureException : erreur réseau/DB temporaire uniquement
     */
    @Bean("stepCalculMgaBalance")
    public Step stepCalculMgaBalance(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     @Qualifier("mgaBalanceReader") ItemReader<AccountEntity> reader,
                                     @Qualifier("mgaBalanceProcessor") ItemProcessor<AccountEntity, AccountEntity> processor,
                                     @Qualifier("mgaBalanceWriter") JpaItemWriter<AccountEntity> writer
    ) {
        return new StepBuilder("stepCalculMgaBalance", jobRepository)
                .<AccountEntity, AccountEntity>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retry(DataAccessResourceFailureException.class)
                .retryLimit(3)
                .build();
    }

    @Bean("jobCalculMgaBalanceFinDeSemaine")
    public Job jobCalculMgaBalanceFinDeSemaine(
            JobRepository jobRepository,
            @Qualifier("stepCalculMgaBalance") Step stepCalculMgaBalance
    ) {
        return new JobBuilder("JobCalculMgaBalanceFinDeSemaine", jobRepository)
                .start(stepCalculMgaBalance)
                .build();
    }

}
