package com.alibou.finance.currency.domain.vo;

import com.alibou.finance.shared.domain.Assert;

public record CurrencyCode(String value) {
    public CurrencyCode{
        Assert.field("CurrencyCode", value).notEmpty();
    }

    public static CurrencyCode from(String code){
        return new CurrencyCode(code);
    }
}
