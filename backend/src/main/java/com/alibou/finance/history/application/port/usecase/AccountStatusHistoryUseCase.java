package com.alibou.finance.history.application.port.usecase;


import com.alibou.finance.history.domain.agregate.AccountStatusHistory;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.shared.application.PageResult;


public interface AccountStatusHistoryUseCase {
    AccountStatusHistory save(AccountStatusHistory AccountStatusHistory);
    PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size);
}
