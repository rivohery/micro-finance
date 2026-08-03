package com.alibou.finance.customer.domain.vo;

import com.alibou.finance.shared.domain.Assert;
import com.alibou.finance.shared.domain.IllegalArgumentException;

public record PhoneNumber(String value) {
    private static final String PHONE_REGEX = "^03[2348][0-9]{7}$";
    public PhoneNumber{
        Assert.field("PhoneNumber", value).notEmpty();
        if(!value.matches(PHONE_REGEX)){
            throw new IllegalArgumentException("Votre numéro de téléphone est invalide");
        }
    }
}
