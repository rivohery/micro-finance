package com.alibou.finance.customer.infrastructure.adapter.in.listener;

import com.alibou.finance.customer.application.usecase.UpdateEmailCustomerUseCase;
import com.alibou.finance.shared.domain.event.UserEmailChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEmailChangedEventListener {
    private final UpdateEmailCustomerUseCase updateEmailCustomerUseCase;

    @ApplicationModuleListener
    public void on(UserEmailChangedEvent userEmailChangedEvent){
        log.info("Received userEmailChangedEvent of {}", userEmailChangedEvent.userId());
        updateEmailCustomerUseCase.execute(
                userEmailChangedEvent.userId(), userEmailChangedEvent.newEmail()
        );
    }
}
