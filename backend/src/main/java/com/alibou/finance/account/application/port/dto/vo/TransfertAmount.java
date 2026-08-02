package com.alibou.finance.account.application.port.dto.vo;

import com.alibou.finance.shared.error.domain.Assert;

import java.math.BigDecimal;

public record TransfertAmount(BigDecimal value) {
    public TransfertAmount{
        Assert.field("Montant du transfert", value).isNumber().positive();
    }
}
