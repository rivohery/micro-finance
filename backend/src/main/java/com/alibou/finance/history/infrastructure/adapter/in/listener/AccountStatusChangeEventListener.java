package com.alibou.finance.history.infrastructure.adapter.in.listener;

import com.alibou.finance.history.application.port.command.AccountStatusHistoryCommand;
import com.alibou.finance.history.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.history.domain.vo.accountStatusHistory.DoingBy;
import com.alibou.finance.history.domain.vo.accountStatusHistory.NewStatus;
import com.alibou.finance.history.domain.vo.accountStatusHistory.OldStatus;
import com.alibou.finance.shared.domain.event.AccountStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountStatusChangeEventListener {

    private final AccountStatusHistoryUseCase accountStatusHistoryUseCase;

    @ApplicationModuleListener
    public void on(AccountStatusChangeEvent event){
        log.info("Received AccountStatusChangeEvent for {}", event.accountId());
        AccountStatusHistoryCommand accountStatusHistoryCommand =  AccountStatusHistoryCommand
                .builder()
                .accountId(event.accountId())
                .doingBy(new DoingBy(event.username().value()))
                .oldStatus(new OldStatus(event.oldStatus()))
                .newStatus(new NewStatus(event.newStatus()))
                .reason(event.reason())
                .build();
        accountStatusHistoryUseCase.save(accountStatusHistoryCommand);
    }
}
