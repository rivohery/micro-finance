package com.alibou.finance.customer.domain.vo;

import com.alibou.finance.shared.error.domain.Assert;

public record ImageUrl(String value) {
    public ImageUrl{
        Assert.field("Image", value).notEmpty();
    }
}
