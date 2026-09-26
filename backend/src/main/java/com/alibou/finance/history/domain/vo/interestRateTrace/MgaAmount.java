package com.alibou.finance.history.domain.vo.interestRateTrace;

import com.alibou.finance.shared.domain.validator.Assert;

import java.math.BigDecimal;

public record MgaAmount(BigDecimal value) {
    public MgaAmount{
        Assert.field("MgaAmount", value).positive();
    }
}
