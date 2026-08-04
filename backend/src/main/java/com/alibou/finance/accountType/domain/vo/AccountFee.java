package com.alibou.finance.accountType.domain.vo;

import com.alibou.finance.shared.domain.Assert;

import java.math.BigDecimal;

public record AccountFee(BigDecimal value) {
    public AccountFee{
        if(value == null){
            value = BigDecimal.ZERO;
        } else {
            Assert.field("Frais du compte", value).isNumber().positive();
        }
    }
}
