package com.alibou.finance.accountType.infrastructure.config;

import com.alibou.finance.accountType.domain.repository.AccountTypeRepository;
import com.alibou.finance.accountType.application.port.AccountTypeUseCase;
import com.alibou.finance.accountType.application.service.AccountTypeServiceApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountTypeServiceApplicationBeanConfig {

    @Bean
    public AccountTypeUseCase createAccountTypeUseCase(AccountTypeRepository accountTypeRepository){
        return new AccountTypeServiceApplication(accountTypeRepository);
    }
}
