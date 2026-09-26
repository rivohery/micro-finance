package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.application.result.PageResult;


import java.util.List;

public interface AccountConsultationUseCase {
    Account findByAccountNumber(AccountNumber accountNumber);
    PageResult<Account> findAllAccountBySearch(String search, int page, int size);
    List<Account> findAllByCustomerId(CustomerId customerId);
    List<Account>findAllByUserConnected(UserId userId);
}
