package com.alibou.finance.account.domain.vo;

import com.alibou.finance.shared.error.domain.Assert;

import java.math.BigDecimal;

public record MgaBalance(BigDecimal value) {
    public MgaBalance{
        Assert.field("MgaBalance", value).positive();
    }

}
