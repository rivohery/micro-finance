package com.alibou.finance.shared.vo.domain;

import com.alibou.finance.shared.error.domain.Assert;

public record OperatorName(String value) {
    public OperatorName{
        Assert.field("Pseudo employée", value).notEmpty();
    }
}
