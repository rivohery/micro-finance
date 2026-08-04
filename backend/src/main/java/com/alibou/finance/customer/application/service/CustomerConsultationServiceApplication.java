package com.alibou.finance.customer.application.service;

import com.alibou.finance.account.application.port.usecase.AccountConsultationUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.customer.application.port.CustomerConsultationUseCase;
import com.alibou.finance.customer.application.model.DetailCustomerWithAccount;
import com.alibou.finance.customer.domain.exception.CustomerNotFoundException;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.application.PageResult;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomerConsultationServiceApplication implements CustomerConsultationUseCase {
    private final CustomerRepository customerRepository;
    private final AccountConsultationUseCase accountConsultationService;

    @Override
    public Customer findCustomerDetailsById(CustomerId customerId) {
        return customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: identifiant customerId invalide")
        );
    }

    @Override
    public PageResult<Customer> findAllEnableCustomerBySearch(String search,  int page, int size) {
        return customerRepository.fetchAllEnableCustomerBySearchBegin(search, page, size);
    }

    @Override
    public PageResult<Customer> findAllCustomerBySearchStart(String search,  int page, int size) {
        return customerRepository.findAllCustomerBySearchBegin(search, page, size);
    }



    @Override
    public DetailCustomerWithAccount findCustomerWithAccounts(CustomerId customerId) {
        Customer customer = getCustomerById(customerId);

        List<Account>accounts = accountConsultationService.findAllByCustomerId(customerId);
        return DetailCustomerWithAccount.builder()
                .customer(customer)
                .accounts(accounts)
                .build();
    }

    @Override
    public CustomerId findCustomerIdByUser(User user) {
        return customerRepository.findCustomerIdByUser(user).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: aucun client correspond au utilisateur connecté")
        );
    }

    private Customer getCustomerById(CustomerId customerId){
        return customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: identifiant client invalide")
        );
    }
}
