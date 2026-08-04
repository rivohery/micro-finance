package com.alibou.finance.log.infrastructure.transactional;

import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.log.application.port.input.AccountStatusHistoryInput;
import com.alibou.finance.log.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.shared.application.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountStatusHistoryUseCaseProxy {

    private final AccountStatusHistoryUseCase accountStatusHistoryUseCase;

    @Transactional
    public AccountStatusHistory save(AccountStatusHistoryInput accountStatusHistoryInput) {
        return accountStatusHistoryUseCase.save(accountStatusHistoryInput);
    }

    @Transactional(readOnly = true)
    public PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size) {
        return accountStatusHistoryUseCase.findAllByAccountId(accountId, page, size);
    }

}
