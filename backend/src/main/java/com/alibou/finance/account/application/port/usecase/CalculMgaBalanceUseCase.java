package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.domain.agregate.Account;

public interface CalculMgaBalanceUseCase {

    Account execute(Account account);
}
