package com.alibou.finance.account.application.port.dto.output;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import lombok.Builder;

@Builder
public record AccountLifeCycleResult(
        Account account,
        AccountStatusHistory history
) {
}
