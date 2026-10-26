package com.alibou.finance.history.domain.vo.accountTypeRate;

import com.alibou.finance.shared.domain.validator.Assert;

import java.util.UUID;

public record AccountTypeRateId(UUID value) {
    public AccountTypeRateId{
        Assert.notNull("AccountTypeRateId", value);
    }

    public static AccountTypeRateId generate(){
        return new AccountTypeRateId(UUID.randomUUID());
    }
}
