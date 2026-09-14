package com.alibou.finance.currency.infrastructure.config;

import com.alibou.finance.currency.application.port.CurrencyUseCase;
import com.alibou.finance.currency.application.service.CurrencyServiceApplication;
import com.alibou.finance.currency.domain.repository.CurrencyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyServiceApplicationBeanConfig {

    @Bean
    public CurrencyUseCase createCurrencyUseCase(CurrencyRepository currencyRepository){
        return new CurrencyServiceApplication(currencyRepository);
    }
}
