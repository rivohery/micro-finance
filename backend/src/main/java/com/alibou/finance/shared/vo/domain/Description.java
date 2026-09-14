package com.alibou.finance.shared.vo.domain;

import com.alibou.finance.shared.domain.Assert;

public record Description(String value) {
    public Description{
        Assert.field("Description", value).notEmpty();
    }
}
