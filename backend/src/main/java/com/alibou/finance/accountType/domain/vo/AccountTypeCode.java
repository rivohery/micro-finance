package com.alibou.finance.accountType.domain.vo;

import com.alibou.finance.shared.domain.Assert;
import com.alibou.finance.shared.domain.IllegalArgumentException;

public record AccountTypeCode(String value) {
    private static final int CODE_LENGTH = 2;
    private static final String CODE_PATTERN = "^[1-9]0$";
    public AccountTypeCode{
        Assert.field("Code du compte", value).size(CODE_LENGTH);
        if(!value.matches(CODE_PATTERN)){
            throw new IllegalArgumentException("Type de compte, code invalide");
        }
    }

}



