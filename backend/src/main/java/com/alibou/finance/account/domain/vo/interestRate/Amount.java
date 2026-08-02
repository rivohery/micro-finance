package com.alibou.finance.account.domain.vo.interestRate;

import com.alibou.finance.shared.error.domain.Assert;

import java.math.BigDecimal;

public record Amount(BigDecimal value) {
    public Amount{
        Assert.field("Amount", value).positive();
    }
}
