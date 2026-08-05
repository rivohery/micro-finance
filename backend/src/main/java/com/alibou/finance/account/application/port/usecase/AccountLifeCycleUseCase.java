package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.dto.input.AccountLifeCycleInput;
import java.util.Map;

public interface AccountLifeCycleUseCase {

    Map<String, Object> activateAccount(AccountLifeCycleInput input);
    Map<String, Object> suspendAccount(AccountLifeCycleInput input);
    Map<String, Object> closeAccount(AccountLifeCycleInput input);

}
