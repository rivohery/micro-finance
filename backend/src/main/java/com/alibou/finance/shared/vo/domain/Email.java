package com.alibou.finance.shared.vo.domain;

import com.alibou.finance.shared.domain.Assert;
import com.alibou.finance.shared.domain.IllegalArgumentException;


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
