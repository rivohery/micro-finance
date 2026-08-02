package com.alibou.finance.account.infrastructure.config;

import com.alibou.finance.account.application.port.usecase.CalculateMonthlyInterestUseCase;
import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import com.alibou.finance.account.infrastructure.batch.InterestItemProcessor;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class InterestRateBatchConfig {

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

    @Bean
    public ItemProcessor<AccountEntity, AccountEntity> accountProcessor(CalculateMonthlyInterestUseCase calculateMonthlyInterestService) {
        return new InterestItemProcessor(calculateMonthlyInterestService);
    }

    // 3. LE WRITER : Sauvegarde automatique des comptes modifiés en BDD
    @Bean
    public JpaItemWriter<AccountEntity> accountWriter(EntityManagerFactory emf) {
        return new JpaItemWriterBuilder<AccountEntity>()
                .entityManagerFactory(emf) // Utilise JPA pour faire les "UPDATE"
                .build();
    }

    // 4. LE STEP : Combine le Reader, Processor et Writer par paquets (chunks) de 100
    @Bean
    public Step stepCalculInteret(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  RepositoryItemReader<AccountEntity> reader,
                                  ItemProcessor<AccountEntity, AccountEntity> processor,
                                  JpaItemWriter<AccountEntity> writer) {
        return new StepBuilder("stepCalculInteret", jobRepository)
                .<AccountEntity, AccountEntity>chunk(100, transactionManager) // Déclare les types <Entrée, Sortie> et la taille du lot
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    // 5. LE JOB
    @Bean
    public Job JobCalculInteretFinDeMois(JobRepository jobRepository, Step stepCalculInteret) {
        return new JobBuilder("JobCalculInteretFinDeMois", jobRepository)
                .start(stepCalculInteret)
                .build();
    }
}
