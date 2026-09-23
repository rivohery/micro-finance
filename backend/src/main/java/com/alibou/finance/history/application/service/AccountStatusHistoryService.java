package com.alibou.finance.history.application.service;

import com.alibou.finance.history.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.history.domain.agregate.AccountStatusHistory;
import com.alibou.finance.history.domain.out.repository.AccountStatusHistoryRepository;
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
