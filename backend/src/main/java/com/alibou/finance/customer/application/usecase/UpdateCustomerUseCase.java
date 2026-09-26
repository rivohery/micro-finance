package com.alibou.finance.customer.application.usecase;

import com.alibou.finance.customer.application.command.UpdateCustomerCommand;
import com.alibou.finance.customer.domain.agregate.Customer;

public interface UpdateCustomerUseCase {
    Customer execute(UpdateCustomerCommand ucd, byte[] contentFile, String fileName);
}
