package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.domain.agregate.Account;

public interface AddMonthlyInterestUseCase {

    Account execute(Account account);
}
