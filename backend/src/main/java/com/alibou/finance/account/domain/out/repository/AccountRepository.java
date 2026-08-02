package com.alibou.finance.account.domain.out.repository;

import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.customer.domain.vo.CustomerId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AccountRepository {

    Account save(Account account);
    Optional<Account> findById(AccountId accountId);
    Optional<Account>findByAccountNumber(AccountNumber accountNumber);
    Page<Account>findAllAccountBySearch(String search, Pageable pageable);
    List<Account> findAllByCustomerId(CustomerId customerId);

}
