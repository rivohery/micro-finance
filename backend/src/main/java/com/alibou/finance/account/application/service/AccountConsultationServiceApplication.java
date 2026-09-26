package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.usecase.AccountConsultationUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.exception.AccountNotFoundException;
import com.alibou.finance.account.domain.out.linker.CustomerLinkerPort;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.application.result.PageResult;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class AccountConsultationServiceApplication implements AccountConsultationUseCase {
    private final AccountRepository accountRepository;
    private final CustomerLinkerPort customerLinkerPort;

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
    public List<Account> findAllByUserConnected(UserId userId) {
        CustomerId customerId = customerLinkerPort.findCustomerIdByUserId(userId);
        return accountRepository.findAllByCustomerId(customerId);
    }
}
