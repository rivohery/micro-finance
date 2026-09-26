package com.alibou.finance.accountType;

import com.alibou.finance.shared.domain.vo.accountType.MinimumBalance;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeName;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;

public record AccountTypeInfo(
        AccountTypeId accountTypeId,
        AccountTypeCode accountTypeCode,
        AccountTypeName accountTypeName,
        InterestRate annualInterestRate,
        MinimumBalance minimumBalance

) {
}
