package com.alibou.finance.shared.domain.vo.account;

import com.alibou.finance.shared.domain.validator.Assert;

import java.util.UUID;


public record AccountId(UUID value) {
    public AccountId{
        Assert.notNull("AccountId", value);
    }

    public static AccountId from(UUID id){
        return new AccountId(id);
    }

    public static AccountId generate(){
        return new AccountId(UUID.randomUUID());
    }
}
