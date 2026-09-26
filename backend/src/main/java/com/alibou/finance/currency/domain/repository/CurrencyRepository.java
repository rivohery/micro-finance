package com.alibou.finance.currency.domain.repository;

import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.currency.CurrencyId;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository {
    Currency save(Currency currency);
    Optional<Currency> findByCode(CurrencyCode code);
    List<Currency>findAll();
    List<Currency>fetchCurrencyEnable();

    boolean existsById(CurrencyId currencyId);
    void deleteById(CurrencyId currencyId);
    Optional<Currency>findById(CurrencyId currencyId);
}
