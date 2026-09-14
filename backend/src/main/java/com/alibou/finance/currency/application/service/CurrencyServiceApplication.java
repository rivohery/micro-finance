package com.alibou.finance.currency.application.service;

import com.alibou.finance.currency.application.port.CurrencyUseCase;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.exception.CurrencyNotFoundException;
import com.alibou.finance.currency.domain.repository.CurrencyRepository;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyId;
import com.alibou.finance.shared.domain.ObjectInvalidException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CurrencyServiceApplication implements CurrencyUseCase {
    private final CurrencyRepository currencyRepository;

    @Override
    public Currency create(Currency currency) {
        currency.generateCurrencyId();
        currency.active();
        return currencyRepository.save(currency);
    }

    @Override
    public Currency findByCode(CurrencyCode code) {
        return currencyRepository.findByCode(code).orElseThrow(
                ()-> new CurrencyNotFoundException("Monnaie introuvable: code de monnaie invalide")
        );
    }

    @Override
    public Currency findById(CurrencyId currencyId) {
        return currencyRepository.findById(currencyId).orElseThrow(
                ()-> new CurrencyNotFoundException("Monnaie introuvable: identifiant de monnaie invalide")
        );
    }

    @Override
    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    @Override
    public List<Currency> fetchEnableCurrency() {
        return currencyRepository.fetchCurrencyEnable();
    }

    @Override
    public Currency update(Currency currency) {
        return currencyRepository.save(currency);
    }

    @Override
    public void deleteById(CurrencyId currencyId) {
        boolean isExists = currencyRepository.existsById(currencyId);
        if(!isExists){
            throw new ObjectInvalidException("Identifiant currencyId invalide");
        }
        currencyRepository.deleteById(currencyId);
    }
}
