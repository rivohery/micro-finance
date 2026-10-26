package com.alibou.finance.history.domain.vo.accountStatusHistory;

import com.alibou.finance.shared.domain.validator.Assert;

import java.time.LocalDateTime;

public record DoingAt(LocalDateTime value) {
    public DoingAt{
        Assert.notNull("DoingAt", value);
    }
    public static DoingAt now(){
        return new DoingAt(LocalDateTime.now());
    }
}
