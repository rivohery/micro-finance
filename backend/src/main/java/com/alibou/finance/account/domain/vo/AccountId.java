package com.alibou.finance.account.domain.vo;

import com.alibou.finance.shared.domain.Assert;

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
