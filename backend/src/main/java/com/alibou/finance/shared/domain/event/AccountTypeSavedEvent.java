package com.alibou.finance.shared.domain.event;

import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;

public record AccountTypeSavedEvent(
        AccountTypeId accountTypeId,
        AccountTypeCode accountTypeCode,
        InterestRate annualInterestRate
) {
}
