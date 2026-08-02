package com.alibou.finance.log.domain.repository;

import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.account.domain.vo.AccountId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountStatusHistoryRepository {
    AccountStatusHistory save(AccountStatusHistory accountStatusHistory);
    Page<AccountStatusHistory>findAllByAccountId(AccountId accountId, Pageable pageable);
}
