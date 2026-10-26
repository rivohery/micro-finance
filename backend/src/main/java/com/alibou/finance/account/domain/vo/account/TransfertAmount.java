package com.alibou.finance.account.domain.vo.account;

import com.alibou.finance.shared.domain.validator.Assert;

import java.math.BigDecimal;

public record TransfertAmount(BigDecimal value) {
    public TransfertAmount{
        Assert.field("Montant du transfert", value).isNumber().positive();
    }
}
