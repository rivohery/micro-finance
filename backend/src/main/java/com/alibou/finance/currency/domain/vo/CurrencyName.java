package com.alibou.finance.currency.domain.vo;

import com.alibou.finance.shared.domain.Assert;

public record CurrencyName(String value) {
    public CurrencyName{
        Assert.field("Monnaie", value).notEmpty();
    }
}
