package com.alibou.finance.log.domain.vo;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.shared.error.domain.Assert;

public record NewStatus(AccountStatusEnum value) {
    public NewStatus{
        Assert.notNull("NewStatus", value);
    }
}
