package com.alibou.finance.customer.domain.vo;

import com.alibou.finance.shared.domain.Assert;
import com.alibou.finance.shared.domain.IllegalArgumentException;

public record Occupation(String value) {
    private static final String ALPHABETIC_REGEX = "^[a-zA-ZÀ-ÿ]+$";
    public Occupation{
        Assert.field("Occupation", value).notEmpty();
        if(!value.matches(ALPHABETIC_REGEX)){
            throw new IllegalArgumentException("Votre occupation est invalide");
        }
        value = value.trim();
    }
}
