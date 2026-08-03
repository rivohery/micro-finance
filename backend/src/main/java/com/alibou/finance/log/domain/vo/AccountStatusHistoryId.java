package com.alibou.finance.log.domain.vo;

import com.alibou.finance.shared.domain.Assert;

import java.util.UUID;

public record AccountStatusHistoryId(UUID value) {
    public AccountStatusHistoryId {
        Assert.notNull("AccountStatusHistoryId", value);
    }

    public static AccountStatusHistoryId generate(){
        return new AccountStatusHistoryId(UUID.randomUUID());
    }

    public static AccountStatusHistoryId from(UUID id){
        return new AccountStatusHistoryId(id);
    }

}
