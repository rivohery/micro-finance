package com.alibou.finance.currency.domain.vo;

import com.alibou.finance.shared.domain.Assert;

import java.util.UUID;

public record CurrencyId(UUID value) {
    public CurrencyId{
        Assert.notNull("CurrencyId", value);
    }

    public static CurrencyId generate(){
        return new CurrencyId(UUID.randomUUID());
    }

    public static CurrencyId from(UUID id){
        return new CurrencyId(id);
    }
}
