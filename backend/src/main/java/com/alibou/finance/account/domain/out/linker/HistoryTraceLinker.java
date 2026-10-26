package com.alibou.finance.account.domain.out.linker;

import com.alibou.finance.account.domain.agregate.Account;

import java.math.BigDecimal;

public interface HistoryTraceLinker {
    void saveMonthlyInterestRate(Account account, BigDecimal monthlyInterest);

}
