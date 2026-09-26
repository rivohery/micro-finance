package com.alibou.finance.accountType.application.port;

import com.alibou.finance.accountType.application.command.AccountTypeCommand;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;

import java.util.List;

public interface AccountTypeUseCase {
    AccountType create(AccountTypeCommand accountTypeCommand);
    AccountType update(AccountTypeCommand accountTypeCommand, AccountTypeId accountTypeId);
    AccountType findByCode(AccountTypeCode accountTypeCode);
    List<AccountType>findAll();
    void deleteById(AccountTypeId accountTypeId);
    AccountType findById(AccountTypeId accountTypeId);
}
