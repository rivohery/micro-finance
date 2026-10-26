package com.alibou.finance.account.infrastructure.adapter.out.port.linker;

import com.alibou.finance.account.domain.out.linker.CurrencyLinkerPort;
import com.alibou.finance.currency.CurrencyInfo;
import com.alibou.finance.currency.CurrencyProvider;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyLinkerPortAdapter implements CurrencyLinkerPort {

    private final CurrencyProvider currencyProvider;

    @Override
    public CurrencyInfo getCurrencyByCode(CurrencyCode currencyCode) {
        return currencyProvider.getCurrencyByCode(currencyCode);
    }
}
