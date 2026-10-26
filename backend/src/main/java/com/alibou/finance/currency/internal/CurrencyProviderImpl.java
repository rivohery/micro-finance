package com.alibou.finance.currency.internal;

import com.alibou.finance.currency.CurrencyInfo;
import com.alibou.finance.currency.CurrencyProvider;
import com.alibou.finance.currency.application.port.CurrencyUseCase;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyProviderImpl implements CurrencyProvider {

    private final CurrencyUseCase currencyUseCase;
    @Override
    public CurrencyInfo getCurrencyByCode(CurrencyCode currencyCode) {
        Currency currency =  currencyUseCase.findByCode(currencyCode);
        return new CurrencyInfo(
                currency.getCurrencyId(),
                currency.getCode()
        );
    }
}
