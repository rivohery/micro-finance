package com.alibou.finance.account.domain.out.linker;

import com.alibou.finance.accountType.AccountTypeInfo;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;

public interface AccountTypeLinkerPort {
    AccountTypeInfo getAccountTypeByCode(AccountTypeCode accountTypeCode);
}
