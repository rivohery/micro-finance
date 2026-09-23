package com.alibou.finance.history.domain.out.service;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.history.domain.agregate.InterestRateTrace;

import java.math.BigDecimal;

public interface InterestRateTraceFactory {
    InterestRateTrace prepare(Account account, BigDecimal mgaExchangeRate, BigDecimal monthlyInterestRate);
}
