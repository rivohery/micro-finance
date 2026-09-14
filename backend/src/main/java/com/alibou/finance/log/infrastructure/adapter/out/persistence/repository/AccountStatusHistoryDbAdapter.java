package com.alibou.finance.log.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.log.infrastructure.adapter.out.mappers.AccountStatusHistoryMapper;
import com.alibou.finance.log.infrastructure.adapter.out.persistence.entity.AccountStatusHistoryEntity;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.domain.repository.AccountStatusHistoryRepository;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("doingAt").descending());
        Page<AccountStatusHistoryEntity>pageEntities = accountStatusHistoryJpaRepository.findAllByAccountId(accountId.value(), pageable);
        return PageMapper.toPageResult(pageEntities, AccountStatusHistoryMapper::entityToDomain);
    }
}
