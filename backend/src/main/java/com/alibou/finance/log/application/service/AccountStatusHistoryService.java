package com.alibou.finance.log.application.service;

import com.alibou.finance.log.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.domain.repository.AccountStatusHistoryRepository;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.shared.application.PageResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountStatusHistoryService implements AccountStatusHistoryUseCase {
    private final AccountStatusHistoryRepository accountStatusHistoryRepository;

    @Override
    public AccountStatusHistory save(AccountStatusHistory accountStatusHistory) {
        return accountStatusHistoryRepository.save(accountStatusHistory);
    }
    @Override
    public PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size) {
        return accountStatusHistoryRepository.findAllByAccountId(accountId, page, size);
    }
}
