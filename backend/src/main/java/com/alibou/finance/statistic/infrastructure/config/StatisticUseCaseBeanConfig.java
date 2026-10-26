package com.alibou.finance.statistic.infrastructure.config;

import com.alibou.finance.statistic.application.StatisticServiceApplication;
import com.alibou.finance.statistic.application.StatisticUseCase;
import com.alibou.finance.statistic.domain.out.AccountStatisticLinker;
import com.alibou.finance.statistic.domain.out.CustomerStatisticLinker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatisticUseCaseBeanConfig {

    @Bean
    public StatisticUseCase createStatisticUseCaseBean(
            AccountStatisticLinker accountStatisticLinker,
            CustomerStatisticLinker customerStatisticLinker
    ){
        return new StatisticServiceApplication(accountStatisticLinker, customerStatisticLinker);
    }
}
