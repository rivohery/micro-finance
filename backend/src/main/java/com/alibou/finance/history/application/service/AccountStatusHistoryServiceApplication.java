package com.alibou.finance.history.application.service;

import com.alibou.finance.history.application.port.command.AccountStatusHistoryCommand;
import com.alibou.finance.history.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.history.application.utils.AccountStatusHistoryFactory;
import com.alibou.finance.history.domain.agregate.AccountStatusHistory;
import com.alibou.finance.history.domain.out.repository.AccountStatusHistoryRepository;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.shared.application.result.PageResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountStatusHistoryServiceApplication implements AccountStatusHistoryUseCase {
    private final AccountStatusHistoryRepository accountStatusHistoryRepository;

    @Override
    public AccountStatusHistory save(AccountStatusHistoryCommand command) {
        AccountStatusHistory accountStatusHistory = AccountStatusHistoryFactory.prepareFrom(command);
        return accountStatusHistoryRepository.save(accountStatusHistory);
    }
    @Override
    public PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size) {
        return accountStatusHistoryRepository.findAllByAccountId(accountId, page, size);
    }
}
