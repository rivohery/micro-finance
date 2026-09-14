package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.domain.agregate.Account;

public interface CalculateMonthlyInterestUseCase {

    Account execute(Account account);
}
