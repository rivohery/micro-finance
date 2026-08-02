package com.alibou.finance.account.domain.vo.transaction;

import com.alibou.finance.shared.error.domain.Assert;

public record TargetCurrencyCode(String value) {
    public TargetCurrencyCode{
        Assert.field("Monnaie cible", value).notEmpty();
    }
}
