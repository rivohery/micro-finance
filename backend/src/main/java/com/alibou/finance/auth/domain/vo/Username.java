package com.alibou.finance.auth.domain.vo;

import com.alibou.finance.shared.error.domain.Assert;

public record Username(String value) {
    public Username{
        Assert.field("Pseudo", value).between(4, 30);
        value = value.toLowerCase().trim();
    }
}
