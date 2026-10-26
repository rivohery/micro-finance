package com.alibou.finance.history.infrastructure.adapter.in.listener;

import com.alibou.finance.history.application.port.command.AccountTypeInterestRateCommand;
import com.alibou.finance.history.application.port.usecase.AccountTypeInterestRateUseCase;
import com.alibou.finance.shared.domain.event.AccountTypeSavedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountTypeSavedEventListener {

    private final AccountTypeInterestRateUseCase accountTypeInterestRateUseCase;

    @ApplicationModuleListener
    public void on(AccountTypeSavedEvent event){
        log.info("Received AccountTypeSavedEvent of {}", event.accountTypeId());
        AccountTypeInterestRateCommand cmd =  new AccountTypeInterestRateCommand(
                event.accountTypeId(),
                event.accountTypeCode(),
                event.annualInterestRate()
        );
        accountTypeInterestRateUseCase.create(cmd);
    }
}
