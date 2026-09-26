package com.alibou.finance.history.domain.vo.accountStatusHistory;

import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.validator.Assert;

public record OldStatus(AccountStatusEnum value) {
    public OldStatus{
        Assert.notNull("OldStatus", value);
    }
}
