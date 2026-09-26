package com.alibou.finance.history.infrastructure.config;


import com.alibou.finance.history.application.port.usecase.*;
import com.alibou.finance.history.application.service.*;
import com.alibou.finance.history.domain.out.repository.AccountStatusHistoryRepository;
import com.alibou.finance.history.domain.out.repository.AccountTypeInterestRateRepository;
import com.alibou.finance.history.domain.out.repository.InterestRateTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogApplicationBeanConfig {

    @Bean
    public AccountStatusHistoryUseCase accountStatusHistoryUseCase(AccountStatusHistoryRepository accountStatusHistoryRepository){
        return new AccountStatusHistoryServiceApplication(accountStatusHistoryRepository);
    }

    @Bean
    public InterestRateUseCase interestRateUseCase(InterestRateTraceRepository interestRateTraceRepository){
        return new InterestRateServiceApplication(interestRateTraceRepository);
    }

    @Bean
    public AccountTypeInterestRateUseCase accountTypeInterestRateUseCase(AccountTypeInterestRateRepository accountTypeInterestRateRepository){
        return new AccountTypeInterestRateServiceApplication(accountTypeInterestRateRepository);
    }
}
