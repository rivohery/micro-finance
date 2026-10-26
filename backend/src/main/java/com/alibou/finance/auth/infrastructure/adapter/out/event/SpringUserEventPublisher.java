package com.alibou.finance.auth.infrastructure.adapter.out.event;

import com.alibou.finance.auth.domain.out.event.UserEventPublisher;
import com.alibou.finance.shared.domain.event.UserEmailChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringUserEventPublisher implements UserEventPublisher {

    private final ApplicationEventPublisher springPublisher;

    @Override
    public void publish(UserEmailChangedEvent event) {
        log.info("Publish event to synchronise email client and user of{}", event.userId());
        springPublisher.publishEvent(event);
    }


}
