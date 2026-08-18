package com.alibou.finance.customer.application.port;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.customer.application.output.DetailCustomerWithAccount;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.application.PageResult;

public interface CustomerConsultationUseCase {

    Customer findCustomerDetailsById(CustomerId customerId);
    PageResult<Customer> findAllEnableCustomerBySearch(String search, int page, int size);

    PageResult<Customer> findAllCustomerBySearchStart(String search,  int page, int size);

    CustomerId findCustomerIdByUser(User user);


    DetailCustomerWithAccount findCustomerWithAccounts(CustomerId customerId);

}
