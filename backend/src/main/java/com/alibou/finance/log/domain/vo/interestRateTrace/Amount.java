package com.alibou.finance.log.domain.vo.interestRateTrace;

import com.alibou.finance.shared.domain.Assert;

import java.math.BigDecimal;

public record Amount(BigDecimal value) {
    public Amount{
        Assert.field("Amount", value).positive();
    }
}
