package com.alibou.finance.account.infrastructure.adapter.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

public record ExchangeRateResponse(
        String result,
        @JsonProperty("base_code") String baseCode,
        @JsonProperty("conversion_rates") Map<String, BigDecimal>conversionRates
) { }

/*
* Objet json response from l'api tiers: ExchangeRate-API
* {
	"result": "success",
	"documentation": "https://www.exchangerate-api.com/docs",
	"terms_of_use": "https://www.exchangerate-api.com/terms",
	"time_last_update_unix": 1585267200,
	"time_last_update_utc": "Fri, 27 Mar 2020 00:00:01 +0000",
	"time_next_update_unix": 1585353700,
	"time_next_update_utc": "Sat, 28 Mar 2020 00:00:01 +0000",
	"base_code": "USD",
	"conversion_rates": {
		"USD": 1,
		"AUD": 1.4817,
		"BGN": 1.7741,
		"CAD": 1.3168,
		"CHF": 0.9776,
		"CNY": 6.9454,
		"EUR": 0.9069,
		"GBP": 0.7620,
		"MGA": 4500.50
	}
}*/
