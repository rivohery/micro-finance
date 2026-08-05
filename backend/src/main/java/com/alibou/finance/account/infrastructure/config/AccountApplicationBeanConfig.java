package com.alibou.finance.account.infrastructure.config;

import com.alibou.finance.account.application.port.usecase.*;
import com.alibou.finance.account.application.service.*;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.AccountNumberGenerator;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.service.ReferenceGenerator;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationPort;
import com.alibou.finance.accountType.application.port.AccountTypeUseCase;
import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.currency.application.port.CurrencyUseCase;
import com.alibou.finance.customer.application.port.CustomerConsultationUseCase;
import com.alibou.finance.customer.application.port.CustomerLifeCycleUseCase;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.log.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.log.application.port.usecase.InterestRateUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountApplicationBeanConfig {

    @Bean
    public AccountConsultationUseCase createAccountConsultationUseCaseBean(AccountRepository accountRepository, CustomerRepository customerRepository){
        return new AccountConsultationServiceApplication(accountRepository, customerRepository);
    }

    @Bean
    public AccountLifeCycleUseCase createAccountLifeCycleUseCaseBean(AccountRepository accountRepository, UserUseCase userUseCase, AccountStatusHistoryUseCase accountStatusHistoryUseCase){
        return new AccountLifeCycleServiceApplication(accountRepository, userUseCase, accountStatusHistoryUseCase);
    }
    @Bean
    public CreateNewAccountUseCase buildCreateNewAccountUseCaseBean(
            CustomerLifeCycleUseCase customerLifeCycleUseCase,
            AccountTypeUseCase accountTypeUseCase,
            CurrencyUseCase currencyUseCase,
            AccountNumberGenerator accountNumberGenerator,
            CurrencyExchangePort currencyExchangePort,
            AccountRepository accountRepository
    ){
        return new CreateNewAccountServiceApplication(
                customerLifeCycleUseCase,
                accountTypeUseCase,
                currencyUseCase,
                accountNumberGenerator,
                currencyExchangePort,
                accountRepository
        );
    }

    @Bean
    public AccountTransactionUseCase createAccountTransactionUseCaseBean(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            CurrencyExchangePort currencyExchangePort,
            CustomerConsultationUseCase customerService,
            ReferenceGenerator referenceGenerator,
            TransfertConfirmationPort transfertConfirmationService
    ){
        return new AccountTransactionServiceApplication(
                transactionRepository,
                accountRepository,
                currencyExchangePort,
                customerService,
                referenceGenerator,
                transfertConfirmationService
        );
    }
    @Bean
    public CalculateMonthlyInterestUseCase createCalculateMonthlyInterestUseCaseBean(
            TransactionRepository transactionRepository,
            CurrencyExchangePort currencyExchangePort,
            InterestRateUseCase interestRateUseCase
    ){
        return new CalculateMonthlyInterestServiceApplication(transactionRepository, currencyExchangePort, interestRateUseCase);
    }
}
