package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.customer.domain.vo.CustomerId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountConsultationUseCase {
    Account findByAccountNumber(AccountNumber accountNumber);
    Page<Account> findAllAccountBySearch(String search, Pageable pageable);
    List<Account> findAllByCustomerId(CustomerId customerId);
    List<Account>findAllByUserConnected(User user);
}
