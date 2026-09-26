package com.alibou.finance.accountType.domain.out.repository;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;

import java.util.List;
import java.util.Optional;

public interface AccountTypeRepository {
    AccountType save(AccountType accountType);
    Optional<AccountType> findByCode(AccountTypeCode code);
    List<AccountType> findAll();

    boolean existsById(AccountTypeId accountTypeId);
    Optional<AccountType>findById(AccountTypeId accountTypeId);
    void deleteById(AccountTypeId accountTypeId);

    boolean existsByCode(AccountTypeCode accountTypeCode);

}
