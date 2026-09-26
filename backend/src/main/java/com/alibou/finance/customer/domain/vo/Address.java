package com.alibou.finance.customer.domain.vo;

import com.alibou.finance.shared.domain.validator.Assert;

public record Address(String value, String city, String zipCode, String country) {
    public Address{
        Assert.field("Adresse", value).notEmpty();
        Assert.field("city", city).notEmpty();
        Assert.field("zipCode", zipCode).notEmpty();
        Assert.field("country", country).notEmpty();
    }
}
