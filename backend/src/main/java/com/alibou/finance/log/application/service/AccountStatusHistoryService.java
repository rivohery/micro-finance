package com.alibou.finance.log.application.service;

import com.alibou.finance.log.application.port.input.AccountStatusHistoryInput;
import com.alibou.finance.log.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.domain.repository.AccountStatusHistoryRepository;
import com.alibou.finance.account.domain.vo.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountStatusHistoryService implements AccountStatusHistoryUseCase {
    private final AccountStatusHistoryRepository accountStatusHistoryRepository;

    @Override
    @Transactional
    public AccountStatusHistory save(AccountStatusHistoryInput input) {
        AccountStatusHistory history = AccountStatusHistoryInput.toAgregate(input);
        return accountStatusHistoryRepository.save(history);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<AccountStatusHistory> findAllByAccountId(AccountId accountId, Pageable pageable) {
        return accountStatusHistoryRepository.findAllByAccountId(accountId, pageable);
    }
}
