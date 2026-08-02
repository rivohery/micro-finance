package com.alibou.finance.customer.domain.vo;

import com.alibou.finance.customer.domain.model.CustomerStatus;
import com.alibou.finance.shared.error.domain.Assert;

public record Status(CustomerStatus value) {
    public Status{
        Assert.notNull("Status", value);
    }

    public static Status pending(){
        return new Status(CustomerStatus.PENDING);
    }

    public static Status suspended(){
        return new Status(CustomerStatus.SUSPENDED);
    }

    public static Status active(){
        return new Status(CustomerStatus.ACTIVE);
    }

    public static Status close(){
        return new Status(CustomerStatus.CLOSED);
    }
}
