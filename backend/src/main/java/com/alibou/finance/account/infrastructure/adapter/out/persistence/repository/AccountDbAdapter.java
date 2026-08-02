package com.alibou.finance.account.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.AccountMapper;
import com.alibou.finance.customer.domain.vo.CustomerId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountDbAdapter implements AccountRepository {
    private final AccountJpaRepository accountJpaRepository;

    @Override
    public Account save(Account account) {
        var accountEntity = AccountMapper.domainToEntity(account);
        return AccountMapper.entityToDomain(accountJpaRepository.save(accountEntity));
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
       return  accountJpaRepository.findById(accountId.value()).map(AccountMapper::entityToDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(AccountNumber accountNumber) {
        return accountJpaRepository.findByAccountNumber(accountNumber.value()).map(AccountMapper::entityToDomain);
    }

    @Override
    public Page<Account> findAllAccountBySearch(String search, Pageable pageable) {
        return accountJpaRepository.getAllAccountByAccountNumberBegin(search, pageable).map(AccountMapper::domainFromProjection);
    }

    @Override
    public List<Account> findAllByCustomerId(CustomerId customerId) {
        return accountJpaRepository.getAllByCustomerId(customerId.value())
                .stream()
                .map(AccountMapper::domainFromProjection)
                .toList();
    }

}
