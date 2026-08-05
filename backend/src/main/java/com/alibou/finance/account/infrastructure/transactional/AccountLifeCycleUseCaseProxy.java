package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.account.application.port.dto.input.AccountLifeCycleInput;
import com.alibou.finance.account.application.port.usecase.AccountLifeCycleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountLifeCycleUseCaseProxy {

    private final AccountLifeCycleUseCase accountLifeCycleUseCase;

    @Transactional
    public Map<String, Object> activateAccount(AccountLifeCycleInput input) {
        return accountLifeCycleUseCase.activateAccount(input);
    }

    @Transactional
    public Map<String, Object> suspendAccount(AccountLifeCycleInput input) {
        return accountLifeCycleUseCase.suspendAccount(input);
    }

    @Transactional
    public Map<String, Object> closeAccount(AccountLifeCycleInput input) {
        return accountLifeCycleUseCase.closeAccount(input);
    }

}
