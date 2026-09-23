package com.alibou.finance.history.domain.out.repository;

import com.alibou.finance.history.domain.agregate.AccountStatusHistory;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.shared.application.PageResult;

public interface AccountStatusHistoryRepository {
    AccountStatusHistory save(AccountStatusHistory accountStatusHistory);
    PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size);
}
