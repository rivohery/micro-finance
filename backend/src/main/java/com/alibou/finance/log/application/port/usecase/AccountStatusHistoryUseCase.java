package com.alibou.finance.log.application.port.usecase;


import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.shared.application.PageResult;


public interface AccountStatusHistoryUseCase {
    AccountStatusHistory save(AccountStatusHistory AccountStatusHistory);
    PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size);
}
