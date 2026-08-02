package com.alibou.finance.shared.vo.domain;

import com.alibou.finance.shared.error.domain.Assert;
import com.alibou.finance.shared.error.domain.IllegalArgumentException;


public record Email(String value) {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    public Email{
        Assert.field("Email", value).notEmpty();
        if(!value.matches(EMAIL_PATTERN)){
            throw new IllegalArgumentException("Le format de l'adresse email est invalide");
        }
        value = value.toLowerCase().trim();
    }

}
