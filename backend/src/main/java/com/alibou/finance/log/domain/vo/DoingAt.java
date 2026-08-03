package com.alibou.finance.log.domain.vo;

import com.alibou.finance.shared.domain.Assert;

import java.time.LocalDateTime;

public record DoingAt(LocalDateTime value) {
    public DoingAt{
        Assert.notNull("DoingAt", value);
    }
    public static DoingAt now(){
        return new DoingAt(LocalDateTime.now());
    }
}
