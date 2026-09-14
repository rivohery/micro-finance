package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.usecase.AccountConsultationUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.exception.AccountNotFoundException;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.customer.domain.exception.CustomerNotFoundException;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.application.PageResult;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class AccountConsultationServiceApplication implements AccountConsultationUseCase {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Account findByAccountNumber(AccountNumber accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                ()-> new AccountNotFoundException(String.format("Compte introuvable: numéros du compte invalide: %s", accountNumber.value()))
        );
    }

    @Override
    public PageResult<Account> findAllAccountBySearch(String search, int page, int size) {
        return accountRepository.findAllAccountBySearch(search, page, size);
    }

    @Override
    public List<Account> findAllByCustomerId(CustomerId customerId) {
        return accountRepository.findAllByCustomerId(customerId);
    }

    @Override
    public List<Account> findAllByUserConnected(User user) {
        CustomerId customerId = customerRepository.findCustomerIdByUser(user).orElseThrow(
                () -> new CustomerNotFoundException("Aucun ID client correspond au utilisateur connecté")
        );
        return accountRepository.findAllByCustomerId(customerId);
    }
}
