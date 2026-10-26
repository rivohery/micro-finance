package com.alibou.finance.accountType;

import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;

public interface AccountTypeProvider {
    AccountTypeInfo findAccountTypeByCode(AccountTypeCode accountTypeCode);
}
