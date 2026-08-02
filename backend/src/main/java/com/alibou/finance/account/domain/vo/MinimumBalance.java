package com.alibou.finance.account.domain.vo;

import com.alibou.finance.shared.error.domain.Assert;

import java.math.BigDecimal;

public record MinimumBalance(BigDecimal value) {

    public MinimumBalance {
        if(value == null){
            value = BigDecimal.ZERO;
        } else {
            Assert.field("Balance minimal", value).isNumber().positive();
        }
    }

    public static MinimumBalance setNull(){
        return new MinimumBalance(BigDecimal.ZERO);
    }
}
