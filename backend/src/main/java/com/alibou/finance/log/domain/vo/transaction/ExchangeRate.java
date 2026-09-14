package com.alibou.finance.log.domain.vo.transaction;

import com.alibou.finance.shared.domain.Assert;

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
