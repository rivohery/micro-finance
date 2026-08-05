package com.alibou.finance.account.infrastructure.config;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import com.alibou.finance.account.infrastructure.batch.InterestItemProcessor;
import com.alibou.finance.account.infrastructure.transactional.CalculateMonthlyInterestUseCaseProxy;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.SynchronizedItemReader;
import org.springframework.batch.item.support.builder.SynchronizedItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class InterestRateBatchConfig {

    // 1. READER: RepositoryItemReader classique (non thread-safe)
    @Bean
    public RepositoryItemReader<AccountEntity> accountReader(AccountJpaRepository accountJpaRepository){
        Map<String, Sort.Direction> triMap = new HashMap<>();
        triMap.put("createdDate", Sort.Direction.ASC);

        return new RepositoryItemReaderBuilder<AccountEntity>()
                .name("accountReader")
                .repository(accountJpaRepository)
                .methodName("getAllByAccountTypeEntityCodeIn")
                .arguments(Set.of("20","30"), AccountStatusEnum.CLOSED)//pour le paramètre Pageable;spring batch s'en charge automatiquement via pageSize()
                .pageSize(100) // Lit les comptes par paquets de 100
                .sorts(triMap)
                .build();
    }

    // 2. Wrapper Thread-Safe => Rends n'importe quel reader non thread-safe utilisable en environnement multithread
    @Bean
    public SynchronizedItemReader<AccountEntity> synchronizedAccountReader(RepositoryItemReader<AccountEntity> accountReader) {
        return new SynchronizedItemReaderBuilder<AccountEntity>()
                .delegate(accountReader)
                .build();
    }

    // 3. LE PROCESSOR 
    @Bean
    public ItemProcessor<AccountEntity, AccountEntity> accountProcessor(CalculateMonthlyInterestUseCaseProxy calculateMonthlyInterestService) {
        return new InterestItemProcessor(calculateMonthlyInterestService);
    }

    // 4. LE WRITER : Sauvegarde automatique des comptes modifiés en BDD
    @Bean
    public JpaItemWriter<AccountEntity> accountWriter(EntityManagerFactory emf) {
        return new JpaItemWriterBuilder<AccountEntity>()
                .entityManagerFactory(emf) // Utilise JPA pour faire les "UPDATE"
                .build();
    }

    // 5. TaskExecutor : Rendre le step Multithreading
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);   // 5 threads actifs en permanence
        executor.setMaxPoolSize(10);   // Jusqu'à 10 threads si besoin
        executor.setQueueCapacity(25); // File d'attente
        executor.setThreadNamePrefix("batch-thread-");
        executor.initialize();
        return executor;
    }

    // 6. LE STEP : Combine le Reader, Processor et Writer par paquets (chunks) de 100
    @Bean
    public Step stepCalculInteret(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  SynchronizedItemReader<AccountEntity> synchronizedAccountReader,
                                  ItemProcessor<AccountEntity, AccountEntity> accountProcessor,
                                  JpaItemWriter<AccountEntity> accountWriter,
                                  TaskExecutor taskExecutor) {
        return new StepBuilder("stepCalculInteret", jobRepository)
                .<AccountEntity, AccountEntity>chunk(100, transactionManager) // Déclare les types <Entrée, Sortie> et la taille du lot
                .reader(synchronizedAccountReader)
                .processor(accountProcessor)
                .writer(accountWriter)
                .faultTolerant()
                .retry(OptimisticLockingFailureException.class) //exception lier au blocage temporaire de la base de données
                .retryLimit(3) // Réessaie jusqu'à 3 fois
                .taskExecutor(taskExecutor) // <--- active le multithreading !
                .build();
    }

    // 7. LE JOB
    @Bean
    public Job JobCalculInteretFinDeMois(JobRepository jobRepository, Step stepCalculInteret) {
        return new JobBuilder("JobCalculInteretFinDeMois", jobRepository)
                .start(stepCalculInteret)
                .build();
    }
}
