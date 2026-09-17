package com.alibou.finance.account.infrastructure.batch.config;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.batch.processor.InterestItemProcessor;
import com.alibou.finance.account.infrastructure.transactional.AddMonthlyInterestUseCaseProxy;
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
public class InterestRateBatchConfig {

    /**
     * READER : mono-thread, donc JpaPagingItemReader est sûr.
     * - pageSize=100 : lit par paquets de 100 comptes
     * - transacted=false : le reader utilise la transaction du chunk (évite "Transaction already active")
     * - saveState=true : sauvegarde la position de lecture pour reprise sur incident
     */
    @Bean("interestReader")
    public JpaPagingItemReader<AccountEntity> interestReader(EntityManagerFactory entityManagerFactory) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("codes", Set.of("20", "30")); // compte business + compte épargne
        parameters.put("status", AccountStatusEnum.ACTIVE);

        return new JpaPagingItemReaderBuilder<AccountEntity>()
                .name("accountReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString(
                        "SELECT a FROM AccountEntity a " +
                                "JOIN FETCH a.currencyEntity c " +
                                "JOIN FETCH a.accountTypeEntity at " +
                                "WHERE at.code IN :codes " +
                                "AND a.accountStatus =:status " +
                                "ORDER BY a.id ASC")
                .parameterValues(parameters)
                .pageSize(100)
                .transacted(false)
                .saveState(true)
                .build();
    }

    @Bean("interestProcessor")
    public ItemProcessor<AccountEntity, AccountEntity> interestProcessor(
            AddMonthlyInterestUseCaseProxy addMonthlyInterestService) {
        return new InterestItemProcessor(addMonthlyInterestService);
    }

    /**
     * WRITER : sauvegarde les comptes modifiés en base (JPA).
     * Il utilise l'EntityManager de la transaction du chunk.
     */
    @Bean("interestWriter")
    public JpaItemWriter<AccountEntity> interestWriter(EntityManagerFactory emf) {
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
    @Bean("stepCalculInterest")
    public Step stepCalculInterest(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            @Qualifier("interestReader") ItemReader<AccountEntity> reader,
            @Qualifier("interestProcessor") ItemProcessor<AccountEntity, AccountEntity> processor,
            @Qualifier("interestWriter") JpaItemWriter<AccountEntity> writer) {

        return new StepBuilder("stepCalculInterest", jobRepository)
                .<AccountEntity, AccountEntity>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retry(DataAccessResourceFailureException.class) // erreur réseau/connexion DB temporaire
                .retryLimit(3)
                .build();
    }

    /**
     * JOB : démarre sur le step unique.
     */
    @Bean("jobCalculInteretFinDeMois")
    public Job jobCalculInteretFinDeMois(
            JobRepository jobRepository,
            @Qualifier("stepCalculInterest") Step step) {
        return new JobBuilder("JobCalculInteretFinDeMois", jobRepository)
                .start(step)
                .build();
    }
}

