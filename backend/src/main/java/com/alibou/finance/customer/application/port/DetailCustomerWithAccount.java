package com.alibou.finance.customer.application.port;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.customer.domain.model.Customer;
import lombok.Builder;

import java.util.List;

@Builder
public record DetailCustomerWithAccount(
     Customer customer,
     List<Account>accounts
) {
}
