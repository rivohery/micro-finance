package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.domain.agregate.AccountType;
import com.alibou.finance.account.domain.vo.AccountTypeId;

import java.util.List;

public interface AccountTypeUseCase {
    AccountType create(AccountType accountType);
    AccountType update(AccountType accountType);
    AccountType findByCode(String accountTypeCode);
    List<AccountType>findAll();

    AccountTypeId deleteById(AccountTypeId accountTypeId);

    AccountType findById(AccountTypeId accountTypeId);
}
