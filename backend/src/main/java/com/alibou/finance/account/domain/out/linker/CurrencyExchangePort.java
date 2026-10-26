package com.alibou.finance.account.domain.out.linker;

import java.math.BigDecimal;

public interface CurrencyExchangePort {
    BigDecimal getExchangeRate(String fromCurrency, String toCurrency);
}
