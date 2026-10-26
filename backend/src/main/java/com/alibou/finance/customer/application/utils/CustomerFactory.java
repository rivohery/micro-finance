package com.alibou.finance.customer.application.utils;

import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.customer.domain.vo.Address;
import com.alibou.finance.customer.application.command.CreateCustomerCommand;
import com.alibou.finance.customer.domain.agregate.Customer;

public class CustomerFactory {

    public static Customer create(CreateCustomerCommand cmd, UserInfo userInfo) {
        return Customer.builder()
                .userId(userInfo.userId())
                .firstName(cmd.firstName())
                .lastName(cmd.lastName())
                .dateOfBirth(cmd.dateOfBirth())
                .phoneNumber(cmd.phoneNumber())
                .email(cmd.email())
                .cin(cmd.cin())
                .occupation(cmd.occupation())
                .address(new Address(cmd.addressValue(), cmd.addressCity(), cmd.addressZipCode(), cmd.addressCountry()))
                .build();

    }
}
