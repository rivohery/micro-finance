package com.alibou.finance.currency;

import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;

public interface CurrencyProvider {
    CurrencyInfo getCurrencyByCode(CurrencyCode currencyCode);
}
