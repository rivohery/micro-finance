package com.alibou.finance.account.domain.vo;

import com.alibou.finance.shared.domain.Assert;
import com.alibou.finance.shared.domain.IllegalOperationException;

import java.math.BigDecimal;

public record Balance(BigDecimal value) {

    public Balance{
        Assert.field("Solde", value).isNumber().positive();
    }

    public static Balance init(){
        return new Balance(BigDecimal.ZERO);
    }

    public Balance add(BigDecimal amount){
        return new Balance(value.add(amount));
    }

    public Balance subtract(BigDecimal amount){
        if(value.compareTo(amount) <= 0){
            throw new IllegalOperationException(String.format("Solde du compte insuffisant : %s", value.doubleValue()));
        }
        return new Balance(value.subtract(amount));
    }
}
