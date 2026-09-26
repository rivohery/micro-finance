package com.alibou.finance.currency.application.port;

import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.currency.CurrencyId;

import java.util.List;

public interface CurrencyUseCase {
    Currency create(Currency currency);
    Currency findByCode(CurrencyCode code);
    Currency findById(CurrencyId currencyId);
    List<Currency> findAll();
    List<Currency>fetchEnableCurrency();
    Currency update(Currency currency);

    void deleteById(CurrencyId currencyId);
}
