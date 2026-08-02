package com.alibou.finance.customer.domain.vo;

import com.alibou.finance.shared.error.domain.Assert;
import com.alibou.finance.shared.error.domain.IllegalArgumentException;

public record FirstName(String value) {

    private static final int MIN_LENGTH = 2;
    private static final String ALPHABETIC_REGEX = "^[a-zA-ZÀ-ÿ]+$";
    public FirstName{
        Assert.field("Prénom", value).notEmpty().minLength(MIN_LENGTH);
        if(!value.matches(ALPHABETIC_REGEX)){
           throw new IllegalArgumentException("Le prénom est invalide");
        }
        value = value.trim();
    }
}
