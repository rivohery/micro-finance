package com.alibou.finance.account.domain.agregate;

import com.alibou.finance.shared.domain.Assert;

import java.math.BigDecimal;

public record OverdraftLimit(BigDecimal value) {

    public OverdraftLimit{
        Assert.field("OverdraftLimit", value).positive();
    }

    public static OverdraftLimit calculate(BigDecimal minimumBalance, BigDecimal exchangeRate) {
        return new OverdraftLimit(minimumBalance.multiply(exchangeRate));
    }
}
