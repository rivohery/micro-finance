package com.alibou.finance.account.infrastructure.adapter.in.listener;

import com.alibou.finance.account.application.port.usecase.ChangeAnnualInterestRateUseCase;
import com.alibou.finance.shared.domain.event.AnnualInterestRateChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AnnualInterestRateChangeEventListener {

    private final ChangeAnnualInterestRateUseCase changeAnnualInterestRateUseCase;

    @ApplicationModuleListener
    public void on(AnnualInterestRateChangeEvent event){
        log.info("Received AnnualInterestRateChangeEvent of {}", event.accountTypeId());
        changeAnnualInterestRateUseCase.execute(event.accountTypeId(), event.interestRate());
    }
}
