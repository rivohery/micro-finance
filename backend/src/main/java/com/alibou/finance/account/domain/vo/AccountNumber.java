package com.alibou.finance.account.domain.vo;

import com.alibou.finance.shared.domain.Assert;
import com.alibou.finance.shared.domain.IllegalArgumentException;

public record AccountNumber(String value) {

    private static final String ACCOUNT_NUMBER_REGEX = "^[01][0-9]{2}-[1-9]0-[0-9]{10}$";
    public AccountNumber{
        Assert.notNull("Numéros du compte", value);
        if(!value.matches(ACCOUNT_NUMBER_REGEX)){
            throw new IllegalArgumentException("Numéros du compte invalide");
        }
    }

    public static AccountNumber from(String accountNumberRaw){
        return new AccountNumber(accountNumberRaw);
    }
}


