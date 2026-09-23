package com.alibou.finance.history.domain.vo.interestRateTrace;

import com.alibou.finance.shared.domain.Assert;

import java.math.BigDecimal;

public record Amount(BigDecimal value) {
    public Amount{
        Assert.field("Amount", value).positive();// Peut-être égale à Zero si aucune transaction n'est faite sur le compte et son solde est encore ZERO
    }
}
