package com.alibou.finance.shared.domain.vo.accountType;

import com.alibou.finance.shared.domain.validator.Assert;

import java.math.BigDecimal;

public record MinimumBalance(BigDecimal value) {

    public MinimumBalance {
        if(value == null){
            value = BigDecimal.ZERO;
        } else {
            Assert.field("Balance minimal", value).isNumber();
        }
    }

    public static MinimumBalance setNull(){
        return new MinimumBalance(BigDecimal.ZERO);
    }
}
