package com.alibou.finance.shared.domain.vo.user;

import com.alibou.finance.shared.domain.validator.Assert;

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
