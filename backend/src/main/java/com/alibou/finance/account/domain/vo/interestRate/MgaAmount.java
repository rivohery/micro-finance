package com.alibou.finance.account.domain.vo.interestRate;

import com.alibou.finance.shared.error.domain.Assert;

import java.math.BigDecimal;

public record MgaAmount(BigDecimal value) {
    public MgaAmount{
        Assert.field("MgaAmount", value).positive();
    }
}
