package com.alibou.finance.log.application.port.usecase;

import com.alibou.finance.log.application.port.input.AccountStatusHistoryInput;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.shared.application.PageResult;


public interface AccountStatusHistoryUseCase {
    AccountStatusHistory save(AccountStatusHistoryInput accountStatusHistoryInput);
    PageResult<AccountStatusHistory> findAllByAccountId(AccountId accountId, int page, int size);
}
