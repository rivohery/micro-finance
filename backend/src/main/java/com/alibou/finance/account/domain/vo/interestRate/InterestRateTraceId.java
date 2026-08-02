package com.alibou.finance.account.domain.vo.interestRate;

import com.alibou.finance.shared.error.domain.Assert;

import java.util.UUID;

public record InterestRateTraceId(UUID value) {
    public InterestRateTraceId{
        Assert.notNull("InterestRateTraceId", value);
    }

    public static InterestRateTraceId from(UUID id){
        return new InterestRateTraceId(id);
    }

    public static InterestRateTraceId generate(){
        return new InterestRateTraceId(UUID.randomUUID());
    }
}
