package com.alibou.finance.customer.infrastructure.transactional;

import com.alibou.finance.customer.application.result.CustomerDetails;
import com.alibou.finance.customer.application.usecase.CustomerConsultationUseCase;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.application.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerConsultationUseCaseProxy {

    private final CustomerConsultationUseCase customerConsultationUseCase;

    @Transactional(readOnly = true)
    public CustomerDetails findCustomerDetailsById(CustomerId customerId) {
        return customerConsultationUseCase.findCustomerDetailsById(customerId);
    }

    @Transactional(readOnly = true)
    public PageResult<Customer> searchOperableCustomer(String search, int page, int size) {
        return customerConsultationUseCase.searchOperableCustomer(search, page, size);
    }

    @Transactional(readOnly = true)
    public PageResult<Customer> fetchAllCustomerBySearchStart(String search, int page, int size) {
        return customerConsultationUseCase.fetchAllCustomerBySearchStart(search, page, size);
    }

    @Transactional(readOnly = true)
    public Customer findCustomerInfo(CustomerId customerId) {
        return customerConsultationUseCase.findCustomerInfo(customerId);
    }

}
