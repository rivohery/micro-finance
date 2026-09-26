package com.alibou.finance.account.domain.vo.account;

import com.alibou.finance.shared.domain.validator.Assert;

import java.math.BigDecimal;

public record MgaBalance(BigDecimal value) {
    public MgaBalance{
        Assert.field("MgaBalance", value).isNumber();
    }

}
