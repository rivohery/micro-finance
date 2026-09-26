package com.alibou.finance.customer.application.usecase;

import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.customer.application.result.CustomerDetails;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.application.result.PageResult;

public interface CustomerConsultationUseCase {

    CustomerDetails findCustomerDetailsById(CustomerId customerId);
    PageResult<Customer> searchOperableCustomer(String search, int page, int size);

    PageResult<Customer> fetchAllCustomerBySearchStart(String search,  int page, int size);

    CustomerId findCustomerIdByUserId(UserId userId);


    Customer findCustomerInfo(CustomerId customerId);

}
