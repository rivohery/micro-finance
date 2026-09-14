package com.alibou.finance.account.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    //On utilise l'api tiers 'ExchangeRate-API' ; url : "https://v6.exchangerate-api.com/v6/TON_API_KEY/latest/USD"

    @Value("${application.api.exchange.url}")
    private String baseUrl;

    @Value("${application.api.exchange.key}")
    private String apiKey;

    @Bean
    public RestClient exchangeRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(baseUrl + apiKey)
                .build();
    }
}
