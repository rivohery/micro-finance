package com.alibou.finance.shared.domain.vo;

import com.alibou.finance.shared.domain.validator.Assert;

public record Description(String value) {
    public Description{
        Assert.field("Description", value).notEmpty();
    }
}
