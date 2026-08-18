package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.account.application.port.usecase.AccountConsultationUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.application.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountConsultationUseCaseProxy {

    private final AccountConsultationUseCase accountConsultationUseCase;

    @Transactional(readOnly = true)
    public Account findByAccountNumber(AccountNumber accountNumber) {
        return accountConsultationUseCase.findByAccountNumber(accountNumber);
    }

    @Transactional(readOnly = true)
    public PageResult<Account> findAllAccountBySearch(String search, int page, int size) {
        return accountConsultationUseCase.findAllAccountBySearch(search, page, size);
    }

    @Transactional(readOnly = true)
    public List<Account> findAllByCustomerId(CustomerId customerId) {
        return accountConsultationUseCase.findAllByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Account> findAllByUserConnected(User user) {
        return accountConsultationUseCase.findAllByUserConnected(user);
    }

}
