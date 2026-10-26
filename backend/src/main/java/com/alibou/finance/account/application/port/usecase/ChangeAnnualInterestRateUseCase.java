package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;

public interface ChangeAnnualInterestRateUseCase {
    void execute(AccountTypeId accountTypeId, InterestRate interestRate);
}
