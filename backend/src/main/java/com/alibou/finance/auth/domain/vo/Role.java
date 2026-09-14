package com.alibou.finance.auth.domain.vo;

import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.shared.domain.Assert;

public record Role(RoleEnum value) {
    public Role{
        Assert.notNull("Rôle", value);
    }

    public static Role employe(){
        return new Role(RoleEnum.EMPLOYE);
    }

}
