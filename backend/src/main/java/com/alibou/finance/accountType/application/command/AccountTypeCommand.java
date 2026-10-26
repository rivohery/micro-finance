package com.alibou.finance.accountType.application.command;

import com.alibou.finance.accountType.domain.vo.*;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeName;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import com.alibou.finance.shared.domain.vo.accountType.MinimumBalance;

public record AccountTypeCommand(
        AccountTypeCode accountTypeCode,
        AccountTypeName accountTypeName,
        AccountFee accountFee,
        InterestRate annualInterestRate,
        MinimumBalance minimumBalance

) {
}

