package com.alibou.finance.accountType.application.port;

import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.domain.vo.AccountTypeCode;
import com.alibou.finance.accountType.domain.vo.AccountTypeId;

import java.util.List;

public interface AccountTypeUseCase {
    AccountType create(AccountType accountType);
    AccountType update(AccountType accountType);
    AccountType findByCode(AccountTypeCode accountTypeCode);
    List<AccountType>findAll();

    AccountTypeId deleteById(AccountTypeId accountTypeId);

    AccountType findById(AccountTypeId accountTypeId);
}
