package com.alibou.finance.shared.domain.vo.user;

import com.alibou.finance.shared.domain.validator.Assert;

import java.util.UUID;

public record CustomerId(UUID value) {
    public CustomerId{
        Assert.notNull("CustomerId", value);
    }

    public static CustomerId generate(){
        return new CustomerId(UUID.randomUUID());
    }

    public static CustomerId from(UUID id){
        return new CustomerId(id);
    }
}
