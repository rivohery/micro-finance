package com.alibou.finance.history.domain.vo.interestRateTrace;

import com.alibou.finance.shared.domain.validator.Assert;

import java.math.BigDecimal;

public record MgaExchangeRate(BigDecimal value) {

    public MgaExchangeRate{
        Assert.field("MgaExchangeRate", value).isNumber();
    }
}
