package com.alibou.finance.account.infrastructure.adapter.out.service;

import com.alibou.finance.account.domain.exception.ThirdPartyServiceException;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.infrastructure.adapter.out.dto.ExchangeRateResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyExchangeAdapter implements CurrencyExchangePort {
    private final RestClient restClient;


    @Override
    @CircuitBreaker(name = "currencyCB", fallbackMethod = "getRateFallback")
    @Retry(name = "currencyRetry")
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }
        ExchangeRateResponse response = restClient.get()
                .uri("/latest/{from}", fromCurrency)
                .retrieve()
                .body(ExchangeRateResponse.class);
        return response.conversionRates().get(toCurrency);
    }

    public BigDecimal getRateFallback(String fromCurrency, String toCurrency, Throwable t) {
        log.error("Erreur API Tiers, utilisation du taux de secours : " + t.getMessage());
        throw new ThirdPartyServiceException("Le service de conversion monétaire est temporairement indisponible. Veuillez réessayer plus tard.");
    }

}
