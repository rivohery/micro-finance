package com.alibou.finance.customer.application.port;

import com.alibou.finance.customer.domain.agregate.Customer;

public interface UpdateCustomerUseCase {
    Customer execute(Customer customer, byte[] contentFile, String fileName);
}
