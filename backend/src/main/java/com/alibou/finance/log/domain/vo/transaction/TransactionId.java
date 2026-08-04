package com.alibou.finance.log.domain.vo.transaction;

import com.alibou.finance.shared.domain.Assert;

import java.util.UUID;

public record TransactionId(UUID value) {
    public TransactionId{
        Assert.notNull("TransactionId", value);
    }

    public static TransactionId from(UUID id){
        return new TransactionId(id);
    }

    public static TransactionId generate(){
        return new TransactionId(UUID.randomUUID());
    }
}
