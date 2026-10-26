package com.alibou.finance.account.domain.vo.account;

import com.alibou.finance.shared.domain.validator.Assert;

import java.util.UUID;

public record ChangedBy(UUID value) {
    public ChangedBy{
        Assert.notNull("ID de l'employé", value);
    }

    public static ChangedBy from(UUID id){
        return new ChangedBy(id);
    }
}
