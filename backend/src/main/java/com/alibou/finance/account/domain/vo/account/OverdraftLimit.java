package com.alibou.finance.account.domain.vo.account;

import com.alibou.finance.shared.domain.validator.Assert;

import java.math.BigDecimal;

public record OverdraftLimit(BigDecimal value) {

    public OverdraftLimit{
        Assert.field("OverdraftLimit", value).isNumber();
    }

    public static OverdraftLimit calculate(BigDecimal minimumBalance, BigDecimal exchangeRate) {
        return new OverdraftLimit(minimumBalance.multiply(exchangeRate));
    }
}
