package com.alibou.finance.currency.domain.vo;

import com.alibou.finance.shared.domain.validator.Assert;

public record CurrencyName(String value) {
    public CurrencyName{
        Assert.field("CurrencyName", value).notEmpty();
    }
}
