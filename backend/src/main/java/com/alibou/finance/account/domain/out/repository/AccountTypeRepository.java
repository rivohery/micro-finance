package com.alibou.finance.account.domain.out.repository;
import com.alibou.finance.account.domain.agregate.AccountType;
import com.alibou.finance.account.domain.vo.AccountTypeId;

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
