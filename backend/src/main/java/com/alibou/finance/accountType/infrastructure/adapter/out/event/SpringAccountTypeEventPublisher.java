package com.alibou.finance.accountType.infrastructure.adapter.out.event;

import com.alibou.finance.accountType.domain.out.event.AccountTypeEventPublisher;
import com.alibou.finance.shared.domain.event.AccountTypeSavedEvent;
import com.alibou.finance.shared.domain.event.AnnualInterestRateChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SpringAccountTypeEventPublisher implements AccountTypeEventPublisher {
    private final ApplicationEventPublisher springPublisher;

    @Override
    public void publish(AnnualInterestRateChangeEvent event) {
        log.info("Publish annualInterestRateEvent from AccountType to change value of annualInterestRate in Account");
        springPublisher.publishEvent(event);
    }

    @Override
    public void publish(AccountTypeSavedEvent event) {
        log.info("Publish AccountTypeSavedEvent from AccountType to save interestRate historic");
        springPublisher.publishEvent(event);
    }
}
