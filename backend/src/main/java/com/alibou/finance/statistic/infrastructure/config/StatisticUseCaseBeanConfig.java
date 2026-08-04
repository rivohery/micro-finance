package com.alibou.finance.statistic.infrastructure.config;

import com.alibou.finance.statistic.application.StatisticServiceApplication;
import com.alibou.finance.statistic.application.StatisticUseCase;
import com.alibou.finance.statistic.domain.service.AccountServicePort;
import com.alibou.finance.statistic.domain.service.CustomerServicePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatisticUseCaseBeanConfig {

    @Bean
    public StatisticUseCase createStatisticUseCaseBean(AccountServicePort accountServicePort, CustomerServicePort customerServicePort){
        return new StatisticServiceApplication(accountServicePort, customerServicePort);
    }
}
