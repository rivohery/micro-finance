package com.alibou.finance.shared.domain.vo.user;

import com.alibou.finance.shared.domain.validator.Assert;
import com.alibou.finance.shared.domain.exception.IllegalArgumentException;


public record Email(String value) {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    public Email{
        Assert.field("Email", value).notEmpty();
        if(!value.matches(EMAIL_PATTERN)){
            throw new IllegalArgumentException("Le format de l'adresse email est invalide");
        }
        value = value.toLowerCase().trim();
    }

    public static Email from(String value){
        return new Email(value);
    }

}
