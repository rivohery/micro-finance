package com.alibou.finance.account.domain.out.service;

import java.math.BigDecimal;

public interface CurrencyExchangePort {
    BigDecimal getExchangeRate(String fromCurrency, String toCurrency);
}
