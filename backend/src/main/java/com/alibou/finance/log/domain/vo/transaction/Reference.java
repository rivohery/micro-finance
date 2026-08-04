package com.alibou.finance.log.domain.vo.transaction;

import com.alibou.finance.shared.domain.Assert;

public record Reference(String value) {
    public Reference{
        Assert.field("Reference", value).notEmpty();
    }

}
