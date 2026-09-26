package com.alibou.finance.history.domain.vo.accountStatusHistory;

import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.validator.Assert;

public record NewStatus(AccountStatusEnum value) {
    public NewStatus{
        Assert.notNull("NewStatus", value);
    }
}
