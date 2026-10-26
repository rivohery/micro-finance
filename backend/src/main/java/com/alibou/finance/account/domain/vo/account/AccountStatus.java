package com.alibou.finance.account.domain.vo.account;

import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.validator.Assert;
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
