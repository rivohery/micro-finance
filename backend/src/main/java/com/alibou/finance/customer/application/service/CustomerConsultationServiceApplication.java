package com.alibou.finance.customer.application.service;


import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.customer.application.result.CustomerDetails;
import com.alibou.finance.customer.application.usecase.CustomerConsultationUseCase;
import com.alibou.finance.customer.domain.exception.CustomerNotFoundException;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.linker.UserLinkerPort;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.application.result.PageResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerConsultationServiceApplication implements CustomerConsultationUseCase {
    private final CustomerRepository customerRepository;
    private final UserLinkerPort userLinkPort;

    @Override
    public CustomerDetails findCustomerDetailsById(CustomerId customerId) {
        Customer customer =  customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: identifiant customerId invalide")
        );
        UserInfo user = userLinkPort.getUserByCustomer(customer);
        return new CustomerDetails(customer, user);
    }

    @Override
    public PageResult<Customer> searchOperableCustomer(String search,  int page, int size) {
        return customerRepository.searchOperableCustomer(search, page, size);
    }

    @Override
    public PageResult<Customer> fetchAllCustomerBySearchStart(String search,  int page, int size) {
        return customerRepository.fetchAllCustomerBySearchStart(search, page, size);
    }

    @Override
    public Customer findCustomerInfo(CustomerId customerId) {
        return customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: identifiant client invalide")
        );
    }

    @Override
    public CustomerId findCustomerIdByUserId(UserId userId) {
        return customerRepository.findCustomerIdByUserId(userId).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: aucun client correspond au utilisateur connecté")
        );
    }

}
