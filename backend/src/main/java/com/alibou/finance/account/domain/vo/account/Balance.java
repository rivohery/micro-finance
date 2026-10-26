package com.alibou.finance.account.domain.vo.account;

import com.alibou.finance.shared.domain.validator.Assert;

import java.math.BigDecimal;

public record Balance(BigDecimal value) {

    public Balance{
        Assert.field("Solde", value).isNumber(); //Solde négatif autorisé
    }

    public static Balance init(){
        return new Balance(BigDecimal.ZERO);
    }

    public Balance add(BigDecimal amount){
        return new Balance(value.add(amount));
    }

    public Balance subtract(BigDecimal amount){
        return new Balance(value.subtract(amount));
    }
}
