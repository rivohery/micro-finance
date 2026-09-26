package com.alibou.finance.account.domain.vo.transaction;

import com.alibou.finance.shared.domain.validator.Assert;

import java.math.BigDecimal;

public record SoldBeforeTransaction(BigDecimal value) {

    public SoldBeforeTransaction{
        Assert.field("SoldBeforeTransaction", value).positive();
    }

    public static SoldBeforeTransaction buildFrom(BigDecimal sold){
        return new SoldBeforeTransaction(sold);
    }
}
