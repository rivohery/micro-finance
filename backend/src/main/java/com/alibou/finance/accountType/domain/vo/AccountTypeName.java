package com.alibou.finance.accountType.domain.vo;

import com.alibou.finance.shared.domain.Assert;

public record AccountTypeName(String value) {
    public AccountTypeName{
        Assert.field("AccountTypeName", value).notEmpty();
        value = value.toUpperCase();
    }

    public static AccountTypeName from(String name){
        return new AccountTypeName(name);
    }
}
