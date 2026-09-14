package com.alibou.finance.log.domain.vo.transaction;

import com.alibou.finance.shared.domain.Assert;

import java.math.BigDecimal;

public record SoldBeforeTransaction(BigDecimal value) {

    public SoldBeforeTransaction{
        Assert.field("SoldBeforeTransaction", value).positive();
    }

    public static SoldBeforeTransaction getFrom(BigDecimal sold){
        return new SoldBeforeTransaction(sold);
    }
}
