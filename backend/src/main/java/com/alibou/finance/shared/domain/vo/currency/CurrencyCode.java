package com.alibou.finance.shared.domain.vo.currency;

import com.alibou.finance.shared.domain.validator.Assert;

public record CurrencyCode(String value) {
    public CurrencyCode{
        Assert.field("CurrencyCode", value).notEmpty();
    }

    public static CurrencyCode from(String code){
        return new CurrencyCode(code);
    }
}
