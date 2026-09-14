package com.alibou.finance.log.domain.vo.interestRateTrace;

import com.alibou.finance.shared.domain.Assert;

import java.math.BigDecimal;

public record MgaAmount(BigDecimal value) {
    public MgaAmount{
        Assert.field("MgaAmount", value).positive();
    }
}
