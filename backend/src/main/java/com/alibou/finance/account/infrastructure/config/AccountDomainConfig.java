package com.alibou.finance.account.infrastructure.config;

import com.alibou.finance.account.application.utils.MonthlyInterestCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AccountDomainConfig {

    @Bean
    public MonthlyInterestCalculator monthlyInterestCalculator() {
        return new MonthlyInterestCalculator();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
