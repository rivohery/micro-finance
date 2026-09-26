package com.alibou.finance.account.infrastructure.adapter.out.port.event;

import com.alibou.finance.account.domain.out.event.AccountStatusEventPublisher;
import com.alibou.finance.shared.domain.event.AccountStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountStatusEventPublisherAdapter implements AccountStatusEventPublisher {

    private final ApplicationEventPublisher springPublisher;

    @Override
    public void publish(AccountStatusChangeEvent event) {
        springPublisher.publishEvent(event);
    }
}
