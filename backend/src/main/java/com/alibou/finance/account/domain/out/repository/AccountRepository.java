package com.alibou.finance.account.domain.out.repository;

import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.NumberAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.SoldeAccountStatisticProj;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.application.result.PageResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);
    Optional<Account> findById(AccountId accountId);
    Optional<Account>findByAccountNumber(AccountNumber accountNumber);
    PageResult<Account> findAllAccountBySearch(String search, int page, int size);
    List<Account> findAllByCustomerId(CustomerId customerId);
    void changeAnnualInterestRate(AccountTypeId accountTypeId, InterestRate interestRate);
    List<NumberAccountStatisticProj> getStatisticNumberOfAccountNoClosed();
    List<SoldeAccountStatisticProj> getAccountStatisticSoldNoClosed();
    Long getNbrTotalOfAccountNoClosed();
    BigDecimal getSoldTotalOfAccountNoClosed();
}
