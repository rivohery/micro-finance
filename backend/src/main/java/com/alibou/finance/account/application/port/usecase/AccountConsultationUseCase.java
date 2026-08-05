package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.application.PageResult;


import java.util.List;

public interface AccountConsultationUseCase {
    Account findByAccountNumber(AccountNumber accountNumber);
    PageResult<Account> findAllAccountBySearch(String search, int page, int size);
    List<Account> findAllByCustomerId(CustomerId customerId);
    List<Account>findAllByUserConnected(User user);
}
