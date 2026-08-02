package com.alibou.finance.log.domain.vo;

import com.alibou.finance.shared.error.domain.Assert;

public record DoingBy(String value) {
    public DoingBy {
        Assert.field("DoingBy", value).notEmpty();
    }

    public static DoingBy from(String username){
        return new DoingBy(username);
    }
}
