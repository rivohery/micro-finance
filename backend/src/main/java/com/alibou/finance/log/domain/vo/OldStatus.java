package com.alibou.finance.log.domain.vo;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.shared.domain.Assert;

public record OldStatus(AccountStatusEnum value) {
    public OldStatus{
        Assert.notNull("OldStatus", value);
    }
}
