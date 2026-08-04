package com.alibou.finance.log.domain.vo.accountStatusHistory;

import com.alibou.finance.shared.domain.Assert;

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
