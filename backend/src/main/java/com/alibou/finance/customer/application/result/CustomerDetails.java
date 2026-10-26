package com.alibou.finance.customer.application.result;

import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.customer.domain.agregate.Customer;

public record CustomerDetails(
        Customer customer,
        UserInfo userInfo
) { }
