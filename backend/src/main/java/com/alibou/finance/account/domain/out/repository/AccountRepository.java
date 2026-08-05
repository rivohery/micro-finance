package com.alibou.finance.account.domain.out.repository;

import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.application.PageResult;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);
    Optional<Account> findById(AccountId accountId);
    Optional<Account>findByAccountNumber(AccountNumber accountNumber);
    PageResult<Account> findAllAccountBySearch(String search, int page, int size);
    List<Account> findAllByCustomerId(CustomerId customerId);

}
