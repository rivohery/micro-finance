package com.alibou.finance.accountType.domain.out.event;

import com.alibou.finance.shared.domain.event.AccountTypeSavedEvent;
import com.alibou.finance.shared.domain.event.AnnualInterestRateChangeEvent;

public interface AccountTypeEventPublisher {
    void publish(AnnualInterestRateChangeEvent event);

    void publish(AccountTypeSavedEvent event);
}
