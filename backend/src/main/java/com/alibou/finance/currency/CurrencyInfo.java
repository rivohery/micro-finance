package com.alibou.finance.currency;

import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.currency.CurrencyId;

public record CurrencyInfo(
        CurrencyId currencyId,
        CurrencyCode currencyCode
) {
}
