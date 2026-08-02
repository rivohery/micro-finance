package com.alibou.finance.log.infrastructure.out.persistence.repository;

import com.alibou.finance.log.infrastructure.out.mappers.AccountStatusHistoryMapper;
import com.alibou.finance.log.infrastructure.out.persistence.entity.AccountStatusHistoryEntity;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.domain.repository.AccountStatusHistoryRepository;
import com.alibou.finance.account.domain.vo.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountStatusHistoryDbAdapter implements AccountStatusHistoryRepository {

    private final AccountStatusHistoryJpaRepository accountStatusHistoryJpaRepository;

    @Override
    public AccountStatusHistory save(AccountStatusHistory accountStatusHistory) {
        AccountStatusHistoryEntity entity = AccountStatusHistoryMapper.domainToEntity(accountStatusHistory);
        return AccountStatusHistoryMapper.entityToDomain(accountStatusHistoryJpaRepository.save(entity));
    }

    @Override
    public Page<AccountStatusHistory> findAllByAccountId(AccountId accountId, Pageable pageable) {
        return accountStatusHistoryJpaRepository.findAllByAccountId(accountId.value(), pageable).map(AccountStatusHistoryMapper::entityToDomain);
    }
}
