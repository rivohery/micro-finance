package com.alibou.finance.auth.domain.vo;

import com.alibou.finance.auth.domain.out.service.PasswordHasher;
import com.alibou.finance.shared.domain.validator.Assert;

public record Password(String value) {
    private static final int MIN_LENGTH = 4;
    private static final String DEFAULT_PSW = "0000";
    public Password{
        Assert.field("Mot de passe", value).minLength(MIN_LENGTH);
    }
    public static Password hash(String rawPassword, PasswordHasher passwordHasher){
        return new Password(passwordHasher.hash(rawPassword));
    }

    public static Password setDefaultPswd(){
        return new Password(DEFAULT_PSW);
    }

}
