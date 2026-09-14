package com.alibou.finance.currency.infrastructure.transactional;


import com.alibou.finance.currency.application.port.CurrencyUseCase;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyUseCaseProxy {

    private final CurrencyUseCase currencyUseCase;

    @Transactional
    public Currency create(Currency currency) {
        return currencyUseCase.create(currency);
    }

    @Transactional(readOnly = true)
    public Currency findByCode(CurrencyCode code) {
        return currencyUseCase.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Currency findById(CurrencyId currencyId) {
        return currencyUseCase.findById(currencyId);
    }

    @Transactional(readOnly = true)
    public List<Currency> findAll() {
        return currencyUseCase.findAll();
    }

    @Transactional(readOnly = true)
    public List<Currency> fetchEnableCurrency() {
        return currencyUseCase.fetchEnableCurrency();
    }

    @Transactional
    public Currency update(Currency currency) {
        return currencyUseCase.update(currency);
    }

    @Transactional
    public void deleteById(CurrencyId currencyId) {
        currencyUseCase.deleteById(currencyId);
    }

}
