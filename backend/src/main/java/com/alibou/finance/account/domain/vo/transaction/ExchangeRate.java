package com.alibou.finance.account.domain.vo.transaction;

import com.alibou.finance.shared.error.domain.Assert;

import java.math.BigDecimal;

public record ExchangeRate(BigDecimal value) {
    public ExchangeRate{
        if(value == null){
            value = BigDecimal.ONE;
        } else {
            Assert.field("ExchangeRate", value).positive();
        }
    }
}
