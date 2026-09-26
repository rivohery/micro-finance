package com.alibou.finance.shared.domain.vo.currency;

import com.alibou.finance.shared.domain.validator.Assert;

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
