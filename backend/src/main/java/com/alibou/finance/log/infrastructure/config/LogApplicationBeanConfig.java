package com.alibou.finance.log.infrastructure.config;

import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.TransactionsReportPort;
import com.alibou.finance.log.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.log.application.port.usecase.InterestRateUseCase;
import com.alibou.finance.log.application.port.usecase.TransactionConsultationUseCase;
import com.alibou.finance.log.application.service.AccountStatusHistoryService;
import com.alibou.finance.log.application.service.InterestRateServiceApplication;
import com.alibou.finance.log.application.service.TransactionConsultationService;
import com.alibou.finance.log.domain.repository.AccountStatusHistoryRepository;
import com.alibou.finance.log.domain.repository.InterestRateTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogApplicationBeanConfig {

    @Bean
    public AccountStatusHistoryUseCase createAccountStatusHistoryUseCaseBean(AccountStatusHistoryRepository accountStatusHistoryRepository){
        return new AccountStatusHistoryService(accountStatusHistoryRepository);
    }

    @Bean
    public InterestRateUseCase createInterestRateUseCaseBean(InterestRateTraceRepository interestRateTraceRepository){
        return new InterestRateServiceApplication(interestRateTraceRepository);
    }

    @Bean
    public TransactionConsultationUseCase createTransactionConsultationUseCaseBean(TransactionRepository transactionRepository, TransactionsReportPort transactionsReport){
        return new TransactionConsultationService(transactionRepository, transactionsReport);
    }
}
