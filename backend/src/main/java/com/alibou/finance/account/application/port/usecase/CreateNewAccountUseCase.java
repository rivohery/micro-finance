package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.command.CreateAccountCommand;
import com.alibou.finance.account.domain.agregate.Account;

public interface CreateNewAccountUseCase {
    Account execute(CreateAccountCommand createAccountCommand);
}
