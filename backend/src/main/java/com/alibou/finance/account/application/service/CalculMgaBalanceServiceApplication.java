package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.usecase.CalculMgaBalanceUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CalculMgaBalanceServiceApplication implements CalculMgaBalanceUseCase {

    private static final String CURRENCY_REFERENCE_CODE = "MGA";
    private final CurrencyExchangePort currencyExchangePort;

    @Override
    public Account execute(Account account) {
        BigDecimal mgaExchangeRate = currencyExchangePort.getExchangeRate(account.getCurrency().getCode().value(), CURRENCY_REFERENCE_CODE);
        account.calculMgaBalance(mgaExchangeRate);
        return account;
    }
}
