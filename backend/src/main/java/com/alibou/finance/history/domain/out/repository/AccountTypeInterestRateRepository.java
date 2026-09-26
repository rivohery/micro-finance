package com.alibou.finance.history.domain.out.repository;

import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.history.domain.agregate.AccountTypeInterestRate;

import java.util.List;
import java.util.Set;

public interface AccountTypeInterestRateRepository {
    List<AccountTypeInterestRate>findRatesByAccountTypeIds(Set<AccountTypeId>accountTypeIds);
    AccountTypeInterestRate save(AccountTypeInterestRate accountTypeInterestRate);
}
