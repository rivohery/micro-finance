package com.alibou.finance.history.application.port.usecase;


import com.alibou.finance.history.application.port.command.AccountStatusHistoryCommand;
import com.alibou.finance.history.domain.agregate.AccountStatusHistory;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.shared.application.result.PageResult;


public interface AccountStatusHistoryUseCase {
    AccountStatusHistory save(AccountStatusHistoryCommand AccountStatusHistory);
    PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size);
}
