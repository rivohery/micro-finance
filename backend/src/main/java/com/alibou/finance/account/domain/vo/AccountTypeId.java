package com.alibou.finance.account.domain.vo;

import com.alibou.finance.shared.error.domain.Assert;

import java.util.UUID;

public record AccountTypeId(UUID value) {
    public AccountTypeId{
        Assert.notNull("accountTypeId", value);
    }

    public static AccountTypeId from(UUID id){
        return new AccountTypeId(id);
    }

    public static AccountTypeId generate(){
        return new AccountTypeId(UUID.randomUUID());
    }
}
