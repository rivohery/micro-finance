package com.alibou.finance.history.infrastructure.transactional;

import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.history.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.history.domain.agregate.AccountStatusHistory;
import com.alibou.finance.shared.application.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountStatusHistoryUseCaseProxy {

    private final AccountStatusHistoryUseCase accountStatusHistoryUseCase;

    @Transactional
    public AccountStatusHistory save(AccountStatusHistory accountStatusHistory) {
        return accountStatusHistoryUseCase.save(accountStatusHistory);
    }

    @Transactional(readOnly = true)
    public PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size) {
        return accountStatusHistoryUseCase.findAllByAccountId(accountId, page, size);
    }

}
