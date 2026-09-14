package com.alibou.finance.auth.domain.vo;

import com.alibou.finance.shared.domain.Assert;

public record Address(String value, String city, String zipCode, String country) {
    public Address{
        Assert.field("Adresse", value).notEmpty();
        Assert.field("city", city).notEmpty();
        Assert.field("zipCode", zipCode).notEmpty();
        Assert.field("country", country).notEmpty();
    }
}
