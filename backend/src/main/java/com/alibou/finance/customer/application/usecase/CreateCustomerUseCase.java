package com.alibou.finance.customer.application.usecase;

import com.alibou.finance.customer.application.command.CreateCustomerCommand;
import com.alibou.finance.customer.domain.agregate.Customer;

public interface CreateCustomerUseCase {
    Customer execute(CreateCustomerCommand createCustomerCommand, byte[] contentFile, String fileName);
}
