package com.alibou.finance.account.domain.vo.transaction;

import com.alibou.finance.shared.domain.validator.Assert;

public record Reference(String value) {
    public Reference{
        Assert.field("Reference", value).notEmpty();
    }

}
