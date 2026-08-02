package com.alibou.finance.account.domain.vo.transaction;

import com.alibou.finance.shared.error.domain.Assert;

import java.math.BigDecimal;

public record OriginalAmount(BigDecimal value) {
    public OriginalAmount {
        Assert.field("Montant", value).positiveStrict();
    }
}
