package com.alibou.finance.history.application.port.command;

import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;

public record AccountTypeInterestRateCommand(
        AccountTypeId accountTypeId,
        AccountTypeCode accountTypeCode,
        InterestRate annualInterestRate
) {
}
