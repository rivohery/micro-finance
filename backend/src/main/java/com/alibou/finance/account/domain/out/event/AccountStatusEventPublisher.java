package com.alibou.finance.account.domain.out.event;

import com.alibou.finance.shared.domain.event.AccountStatusChangeEvent;

public interface AccountStatusEventPublisher {
    void publish(AccountStatusChangeEvent event);
}
