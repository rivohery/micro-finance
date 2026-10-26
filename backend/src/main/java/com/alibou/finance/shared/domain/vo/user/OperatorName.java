package com.alibou.finance.shared.domain.vo.user;

import com.alibou.finance.shared.domain.validator.Assert;

public record OperatorName(String value) {
    public OperatorName{
        Assert.field("Pseudo employée", value).notEmpty();
    }
}
