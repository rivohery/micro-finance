package com.alibou.finance.history.infrastructure.transactional;

import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.history.application.port.command.AccountStatusHistoryCommand;
import com.alibou.finance.history.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.history.domain.agregate.AccountStatusHistory;
import com.alibou.finance.shared.application.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountStatusHistoryUseCaseProxy {

    private final AccountStatusHistoryUseCase accountStatusHistoryUseCase;

    @Transactional
    public AccountStatusHistory save(AccountStatusHistoryCommand command) {
        return accountStatusHistoryUseCase.save(command);
    }

    @Transactional(readOnly = true)
    public PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size) {
        return accountStatusHistoryUseCase.findAllByAccountId(accountId, page, size);
    }

}
