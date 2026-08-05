package com.alibou.finance.customer.infrastructure.transactional;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.customer.application.model.DetailCustomerWithAccount;
import com.alibou.finance.customer.application.port.CustomerConsultationUseCase;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.application.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerConsultationUseCaseProxy {

    private final CustomerConsultationUseCase customerConsultationUseCase;

    @Transactional(readOnly = true)
    public Customer findCustomerDetailsById(CustomerId customerId) {
        return customerConsultationUseCase.findCustomerDetailsById(customerId);
    }

    @Transactional(readOnly = true)
    public PageResult<Customer> findAllEnableCustomerBySearch(String search, int page, int size) {
        return customerConsultationUseCase.findAllEnableCustomerBySearch(search, page, size);
    }

    @Transactional(readOnly = true)
    public PageResult<Customer> findAllCustomerBySearchStart(String search, int page, int size) {
        return customerConsultationUseCase.findAllCustomerBySearchStart(search, page, size);
    }

    @Transactional(readOnly = true)
    public CustomerId findCustomerIdByUser(User user) {
        return customerConsultationUseCase.findCustomerIdByUser(user);
    }

    @Transactional(readOnly = true)
    public DetailCustomerWithAccount findCustomerWithAccounts(CustomerId customerId) {
        return customerConsultationUseCase.findCustomerWithAccounts(customerId);
    }

}
