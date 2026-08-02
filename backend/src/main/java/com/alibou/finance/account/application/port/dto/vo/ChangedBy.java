package com.alibou.finance.account.application.port.dto.vo;

import com.alibou.finance.shared.error.domain.Assert;

import java.util.UUID;

public record ChangedBy(UUID value) {
    public ChangedBy{
        Assert.notNull("ID de l'employé", value);
    }

    public static ChangedBy from(UUID id){
        return new ChangedBy(id);
    }
}
