package com.alibou.finance.log.domain.repository;

import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.shared.application.PageResult;

public interface AccountStatusHistoryRepository {
    AccountStatusHistory save(AccountStatusHistory accountStatusHistory);
    PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size);
}
