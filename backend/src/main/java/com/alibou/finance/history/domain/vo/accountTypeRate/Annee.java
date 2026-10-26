package com.alibou.finance.history.domain.vo.accountTypeRate;

import com.alibou.finance.shared.domain.validator.Assert;

public record Annee(String value) {

    public Annee{
        Assert.field("Annee", value).notEmpty();
    }
}
