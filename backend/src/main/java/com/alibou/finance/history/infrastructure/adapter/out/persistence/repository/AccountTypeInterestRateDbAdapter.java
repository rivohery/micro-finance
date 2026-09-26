package com.alibou.finance.history.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.history.domain.agregate.AccountTypeInterestRate;
import com.alibou.finance.history.domain.out.repository.AccountTypeInterestRateRepository;
import com.alibou.finance.history.infrastructure.adapter.out.mappers.AccountTypeInterestRateMapper;
import com.alibou.finance.history.infrastructure.adapter.out.persistence.entity.AccountTypeInterestRateEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountTypeInterestRateDbAdapter implements AccountTypeInterestRateRepository {

    private final AccountTypeInterestRateJpaRepository accountTypeInterestRateJpaRepository;

    @Override
    public List<AccountTypeInterestRate> findRatesByAccountTypeIds(Set<AccountTypeId> accountTypeIds) {

        return accountTypeInterestRateJpaRepository.findAllByAccountTypeIdIn(accountTypeIds)
                .stream()
                .map(AccountTypeInterestRateMapper::entityToDomain)
                .toList();
    }

    @Override
    public AccountTypeInterestRate save(AccountTypeInterestRate accountTypeInterestRate) {
        AccountTypeInterestRateEntity entity = AccountTypeInterestRateMapper.domainToEntity(accountTypeInterestRate);
        return AccountTypeInterestRateMapper.entityToDomain(accountTypeInterestRateJpaRepository.save(entity));
    }
}
