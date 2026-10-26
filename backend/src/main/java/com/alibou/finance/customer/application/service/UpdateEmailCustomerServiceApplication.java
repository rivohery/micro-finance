package com.alibou.finance.customer.application.service;

import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.customer.application.usecase.UpdateEmailCustomerUseCase;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.exception.CustomerNotFoundException;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.shared.domain.vo.user.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UpdateEmailCustomerServiceApplication implements UpdateEmailCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public void execute(UserId userId, Email email) {
        Customer customer = loadCustomerByUserId(userId);
        customer.updateEmail(email);
        customerRepository.save(customer);
        log.info("Le champ email dans Customer est modifié");
    }

    private Customer loadCustomerByUserId(UserId userId) {
        return customerRepository.getCustomerByUserId(userId).orElseThrow(
                () -> new CustomerNotFoundException("Client inexistant pour cet objet userId")
        );
    }
}
