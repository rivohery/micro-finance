package com.alibou.finance.account.domain.vo;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.shared.error.domain.Assert;
public record AccountStatus(AccountStatusEnum value) {

    public AccountStatus {
        Assert.notNull("Status du compte", value);
    }

    public static AccountStatus pending(){
        return new AccountStatus(AccountStatusEnum.PENDING);
    }

    public static AccountStatus active(){
        return new AccountStatus(AccountStatusEnum.ACTIVE);
    }
    public static AccountStatus suspend(){
        return new AccountStatus(AccountStatusEnum.SUSPENDED);
    }
    public static AccountStatus close(){
        return new AccountStatus(AccountStatusEnum.CLOSED);
    }
}
