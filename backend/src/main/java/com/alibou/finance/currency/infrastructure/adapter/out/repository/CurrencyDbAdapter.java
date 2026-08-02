package com.alibou.finance.currency.infrastructure.adapter.out.repository;

import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.repository.CurrencyRepository;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyId;
import com.alibou.finance.currency.infrastructure.adapter.out.mapper.CurrencyMapper;
import com.alibou.finance.shared.error.domain.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyDbAdapter implements CurrencyRepository {
    private final CurrencyJpaRepository currencyJpaRepository;

    @Override
    public Currency save(Currency currency) {
        var currencyEntity = CurrencyMapper.domainToEntity(currency);
        return CurrencyMapper.entityToDomain(currencyJpaRepository.save(currencyEntity));
    }

    @Override
    public Optional<Currency> findByCode(CurrencyCode code) {
        return currencyJpaRepository.findByCode(code.value()).map(CurrencyMapper::entityToDomain);
    }

    @Override
    public Optional<Currency> findById(CurrencyId currencyId) {
        return currencyJpaRepository.findById(currencyId.value()).map(CurrencyMapper::entityToDomain);
    }

    @Override
    public boolean existsById(CurrencyId currencyId) {
        return currencyJpaRepository.existsById(currencyId.value());
    }

    @Override
    public List<Currency> findAll() {
        return currencyJpaRepository.findAll()
                .stream()
                .map(CurrencyMapper::entityToDomain)
                .toList();
    }

    @Override
    public List<Currency> fetchCurrencyEnable() {
        return currencyJpaRepository.findAllByEnableIsTrue()
                .stream()
                .map(CurrencyMapper::entityToDomain)
                .toList();
    }

    @Override
    public void deleteById(CurrencyId currencyId) {
        try{
            currencyJpaRepository.deleteById(currencyId.value());
        }catch(Exception ex){
            throw new OperationNotPermittedException("Suppression interrompue: "+ ex.getMessage());
        }
    }
}
