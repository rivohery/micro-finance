package com.alibou.finance.accountType.domain.repository;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.domain.vo.AccountTypeId;

import java.util.List;
import java.util.Optional;

public interface AccountTypeRepository {
    AccountType save(AccountType accountType);
    Optional<AccountType> findByCode(String code);
    List<AccountType> findAll();

    boolean existsById(AccountTypeId accountTypeId);
    Optional<AccountType>findById(AccountTypeId accountTypeId);
    AccountTypeId deleteById(AccountTypeId accountTypeId);

}
