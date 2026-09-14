package com.alibou.finance.log.domain.vo.transaction;

import com.alibou.finance.shared.domain.Assert;

public record TargetCurrencyCode(String value) {
    public TargetCurrencyCode{
        Assert.field("Monnaie cible", value).notEmpty();
    }
}
