package com.alibou.finance.shared.domain.vo.user;

import com.alibou.finance.shared.domain.validator.Assert;

public record Username(String value) {
    public Username{
        Assert.field("Pseudo", value).between(4, 30);
        value = value.toLowerCase().trim();
    }
}
