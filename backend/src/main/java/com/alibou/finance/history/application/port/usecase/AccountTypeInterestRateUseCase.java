package com.alibou.finance.history.application.port.usecase;

import com.alibou.finance.history.application.port.command.AccountTypeInterestRateCommand;
import com.alibou.finance.history.domain.agregate.AccountTypeInterestRate;

public interface AccountTypeInterestRateUseCase {
    AccountTypeInterestRate create(AccountTypeInterestRateCommand accountTypeInterestRateCommand);
}
