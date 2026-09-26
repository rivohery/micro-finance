package com.alibou.finance.accountType.infrastructure.config;

import com.alibou.finance.accountType.application.utils.AccountTypeApplicationHelper;
import com.alibou.finance.accountType.domain.out.event.AccountTypeEventPublisher;
import com.alibou.finance.accountType.domain.out.repository.AccountTypeRepository;
import com.alibou.finance.accountType.application.port.AccountTypeUseCase;
import com.alibou.finance.accountType.application.service.AccountTypeServiceApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountTypeServiceApplicationBeanConfig {

    @Bean
    public AccountTypeApplicationHelper accountTypeApplicationHelper(
            AccountTypeRepository accountTypeRepository,
            AccountTypeEventPublisher accountTypeEventPublisher
    ){
        return new AccountTypeApplicationHelper(accountTypeRepository, accountTypeEventPublisher);
    }

    @Bean
    public AccountTypeUseCase createAccountTypeUseCase(
            AccountTypeRepository accountTypeRepository,
            AccountTypeApplicationHelper accountTypeApplicationHelper
    ){
        return new AccountTypeServiceApplication(accountTypeRepository, accountTypeApplicationHelper);
    }
}

