package com.alibou.finance.log.application.port.usecase;

import com.alibou.finance.log.application.port.input.AccountStatusHistoryInput;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.account.domain.vo.AccountId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountStatusHistoryUseCase {
    AccountStatusHistory save(AccountStatusHistoryInput accountStatusHistoryInput);
    Page<AccountStatusHistory>findAllByAccountId(AccountId accountId, Pageable pageable);
}
