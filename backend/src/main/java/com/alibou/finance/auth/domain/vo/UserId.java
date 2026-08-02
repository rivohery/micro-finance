package com.alibou.finance.auth.domain.vo;

import com.alibou.finance.shared.error.domain.Assert;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId{
        Assert.notNull("UserId", value);
    }

    public static UserId generate(){
        return new UserId(UUID.randomUUID());
    }

    public static UserId from(UUID id){
        return new UserId(id);
    }
}
