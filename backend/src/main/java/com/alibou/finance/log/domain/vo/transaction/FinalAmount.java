package com.alibou.finance.log.domain.vo.transaction;

import com.alibou.finance.shared.domain.Assert;

import java.math.BigDecimal;

public record FinalAmount(BigDecimal value) {
    public FinalAmount {
        Assert.field("Montant", value).positiveStrict();
    }

    public static FinalAmount from(BigDecimal from){
        return new FinalAmount(from);
    }
}
