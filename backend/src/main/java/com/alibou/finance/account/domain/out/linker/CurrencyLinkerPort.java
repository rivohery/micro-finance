package com.alibou.finance.account.domain.out.linker;

import com.alibou.finance.currency.CurrencyInfo;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;

public interface CurrencyLinkerPort {
    CurrencyInfo getCurrencyByCode(CurrencyCode currencyCode);
}
