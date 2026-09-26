package com.alibou.finance.account.infrastructure.config;

import com.alibou.finance.account.application.port.usecase.*;
import com.alibou.finance.account.application.service.*;
import com.alibou.finance.account.application.utils.MonthlyInterestCalculator;
import com.alibou.finance.account.domain.out.linker.*;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.AccountNumberGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AccountApplicationBeanConfig {

    @Bean
    public AccountConsultationUseCase accountConsultationUseCase(
            AccountRepository accountRepository,
            CustomerLinkerPort customerLinkerPort
    ){
        return new AccountConsultationServiceApplication(accountRepository, customerLinkerPort);
    }

    @Bean
    public CreateNewAccountUseCase createNewAccountUseCase(
            AccountTypeLinkerPort accountTypeLinkerPort,
            CurrencyLinkerPort currencyLinkerPort,
            CustomerLinkerPort customerLinkerPort,
            AccountNumberGenerator accountNumberGenerator,
            CurrencyExchangePort currencyExchangePort,
            AccountRepository accountRepository
    ){
        return new CreateNewAccountServiceApplication(
                accountTypeLinkerPort,
                currencyLinkerPort,
                customerLinkerPort,
                accountNumberGenerator,
                currencyExchangePort,
                accountRepository
        );
    }

    @Bean
    public AddMonthlyInterestUseCase addMonthlyInterestUseCase(
            TransactionRepository transactionRepository,
            HistoryTraceLinker historyTraceLinker,
            MonthlyInterestCalculator monthlyInterestCalculator,
            Clock clock
    ){
        return new AddMonthlyInterestServiceApplication(
                transactionRepository,
                historyTraceLinker,
                monthlyInterestCalculator,
                clock
        );
    }

    @Bean
    public CalculMgaBalanceUseCase calculMgaBalanceUseCase(CurrencyExchangePort currencyExchangePort){
        return new CalculMgaBalanceServiceApplication(currencyExchangePort);
    }

    @Bean
    public ChangeAnnualInterestRateUseCase changeAnnualInterestRateUseCase(AccountRepository accountRepository){
        return new ChangeAnnualInterestRateServiceApplication(accountRepository);
    }
}
