package com.alibou.finance.shared.domain.vo.accountType;

import com.alibou.finance.shared.domain.validator.Assert;

public record AccountTypeName(String value) {
    public AccountTypeName{
        Assert.field("AccountTypeName", value).notEmpty();
        value = value.toUpperCase();
    }

    public static AccountTypeName from(String name){
        return new AccountTypeName(name);
    }
}
