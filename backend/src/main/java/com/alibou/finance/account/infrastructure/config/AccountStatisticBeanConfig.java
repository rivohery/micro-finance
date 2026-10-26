package com.alibou.finance.account.infrastructure.config;

import com.alibou.finance.account.application.port.usecase.AccountStatisticUseCase;
import com.alibou.finance.account.application.service.AccountStatisticServiceApplication;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountStatisticBeanConfig {

    @Bean
    public AccountStatisticUseCase accountStatisticUseCase(AccountRepository accountRepository){
        return new AccountStatisticServiceApplication(accountRepository);
    }
}
