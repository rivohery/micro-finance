package com.alibou.finance.account.infrastructure.adapter.out.port.persistence.repository;

import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.AccountMapper;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.AccountProjection;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.NumberAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.SoldeAccountStatisticProj;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.application.result.PageResult;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountDbAdapter implements AccountRepository {
    private final AccountJpaRepository accountJpaRepository;

    @Override
    public Account save(Account account) {
        AccountEntity entityToSave;
        Optional<AccountEntity>optional = accountJpaRepository.findById(account.getAccountId().value());
        if(optional.isPresent()){
            entityToSave = AccountMapper.updateEntityFromDomain(account, optional.get());
        } else {
            entityToSave = AccountMapper.domainToEntity(account);
        }
        return AccountMapper.entityToDomain(accountJpaRepository.save(entityToSave));
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
    public PageResult<Account> findAllAccountBySearch(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<AccountProjection>pageEntities = accountJpaRepository.getAllAccountByAccountNumberBegin(search, pageable);
        return PageMapper.toPageResult(pageEntities, AccountMapper::domainFromProjection);
    }

    @Override
    public List<Account> findAllByCustomerId(CustomerId customerId) {
        return accountJpaRepository.getAllByCustomerId(customerId.value())
                .stream()
                .map(AccountMapper::domainFromProjection)
                .toList();
    }

    @Override
    public void changeAnnualInterestRate(AccountTypeId accountTypeId, InterestRate interestRate) {
        List<AccountEntity>accounts = accountJpaRepository.getAccountsNoClosedByTypeId(accountTypeId.value());
        accounts.forEach(ae -> {
            ae.setAnnualInterestRate(interestRate.value());
            accountJpaRepository.save(ae);
        });
    }

    @Override
    public List<NumberAccountStatisticProj> getStatisticNumberOfAccountNoClosed() {
        return accountJpaRepository.getStatisticNumberOfAccountNoClosed();
    }

    @Override
    public List<SoldeAccountStatisticProj> getAccountStatisticSoldNoClosed() {
        return accountJpaRepository.getAccountStatisticSoldNoClosed();
    }

    @Override
    public BigDecimal getSoldTotalOfAccountNoClosed() {
        return accountJpaRepository.getSoldTotalOfAccountNoClosed();
    }

    @Override
    public Long getNbrTotalOfAccountNoClosed() {
        return accountJpaRepository.getNbrTotalOfAccountNoClosed();
    }
}
