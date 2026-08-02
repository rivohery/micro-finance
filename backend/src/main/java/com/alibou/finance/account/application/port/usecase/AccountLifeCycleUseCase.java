package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.dto.input.AccountLifeCycleInput;
import com.alibou.finance.account.domain.agregate.Account;


import java.util.Map;

public interface AccountLifeCycleUseCase {
    Account create(Account account);
    Map<String, Object> activateAccount(AccountLifeCycleInput input);
    Map<String, Object> suspendAccount(AccountLifeCycleInput input);
    Map<String, Object> closeAccount(AccountLifeCycleInput input);

}
