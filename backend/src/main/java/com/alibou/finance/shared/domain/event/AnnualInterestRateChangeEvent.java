package com.alibou.finance.shared.domain.event;

import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;

public record AnnualInterestRateChangeEvent(
        AccountTypeId accountTypeId,
        InterestRate interestRate
) { }
