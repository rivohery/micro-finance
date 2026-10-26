package com.alibou.finance.customer.domain.vo;

import com.alibou.finance.customer.domain.agregate.CustomerStatusEnum;
import com.alibou.finance.shared.domain.validator.Assert;

public record CustomerStatus(CustomerStatusEnum value) {
    public CustomerStatus {
        Assert.notNull("Status", value);
    }

    public static CustomerStatus pending(){
        return new CustomerStatus(CustomerStatusEnum.PENDING_KYC);
    }

    public static CustomerStatus suspended(){
        return new CustomerStatus(CustomerStatusEnum.SUSPENDED);
    }

    public static CustomerStatus active(){
        return new CustomerStatus(CustomerStatusEnum.ACTIVE);
    }

    public static CustomerStatus close(){
        return new CustomerStatus(CustomerStatusEnum.CLOSED);
    }
}
