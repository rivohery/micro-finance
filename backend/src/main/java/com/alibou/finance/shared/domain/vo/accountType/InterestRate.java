package com.alibou.finance.shared.domain.vo.accountType;

import com.alibou.finance.shared.domain.validator.Assert;

import java.math.BigDecimal;

public record InterestRate(BigDecimal value) {

    public InterestRate {
        if(value == null){
            value = BigDecimal.ZERO;
        } else {
            Assert.field("Taux d'intérêts", value).isNumber().positive();
        }
    }

    public static InterestRate setNull(){
        return new InterestRate(BigDecimal.ZERO);
    }
}
