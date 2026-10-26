package com.alibou.finance.account.infrastructure.config;

import com.alibou.finance.account.application.port.usecase.ActivateAccountUseCase;
import com.alibou.finance.account.application.port.usecase.CloseAccountUseCase;
import com.alibou.finance.account.application.port.usecase.ReactivateAccountUseCase;
import com.alibou.finance.account.application.port.usecase.SuspendedAccountUseCase;
import com.alibou.finance.account.application.service.ActivateAccountServiceApplication;
import com.alibou.finance.account.application.service.CloseAccountServiceApplication;
import com.alibou.finance.account.application.service.ReactivateAccountServiceApplication;
import com.alibou.finance.account.application.service.SuspendedAccountServiceApplication;
import com.alibou.finance.account.application.utils.AccountLifeCycleExecutor;
import com.alibou.finance.account.domain.out.event.AccountStatusEventPublisher;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountLifeCycleBeanConfig {

    @Bean
    public AccountLifeCycleExecutor accountLifeCycleExecutor(
            AccountRepository accountRepository,
            AccountStatusEventPublisher accountStatusEventPublisher
    ){
        return new AccountLifeCycleExecutor(
                accountRepository,
                accountStatusEventPublisher
        );
    }

    @Bean
    public ActivateAccountUseCase activateAccountUseCase(AccountLifeCycleExecutor accountLifeCycleExecutor){
        return new ActivateAccountServiceApplication(accountLifeCycleExecutor);
    }

    @Bean
    public ReactivateAccountUseCase reactivateAccountUseCase(AccountLifeCycleExecutor accountLifeCycleExecutor){
        return new ReactivateAccountServiceApplication(accountLifeCycleExecutor);
    }

    @Bean
    public SuspendedAccountUseCase suspendedAccountUseCase(AccountLifeCycleExecutor accountLifeCycleExecutor){
        return new SuspendedAccountServiceApplication(accountLifeCycleExecutor);
    }

    @Bean
    public CloseAccountUseCase closeAccountUseCase(AccountLifeCycleExecutor accountLifeCycleExecutor){
        return new CloseAccountServiceApplication(accountLifeCycleExecutor);
    }
}
