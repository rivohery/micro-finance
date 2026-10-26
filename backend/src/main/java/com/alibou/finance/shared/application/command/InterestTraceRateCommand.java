package com.alibou.finance.shared.application.command;

import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;

import java.math.BigDecimal;

public record InterestTraceRateCommand(
        AccountNumber accountNumber,
        InterestRate interestRate,
        CurrencyCode currencyCode,
        BigDecimal amount,
        BigDecimal mgaExchangeRate,
        BigDecimal mgaAmount
) {
}

