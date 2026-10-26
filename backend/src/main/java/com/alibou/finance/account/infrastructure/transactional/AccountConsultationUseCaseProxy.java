package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.account.infrastructure.adapter.in.dto.AccountResponse;
import com.alibou.finance.account.application.port.usecase.AccountConsultationUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.AccountMapper;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.application.result.PageResult;
import com.alibou.finance.shared.domain.vo.user.UserId;
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
    public List<AccountResponse> findAllByCustomerId(CustomerId customerId) {
        return accountConsultationUseCase.findAllByCustomerId(customerId)
                .stream()
                .map(AccountMapper::domainToAccountResponse)
                .toList()
       ;
    }

    @Transactional(readOnly = true)
    public List<Account> findAllByUserConnected(UserId userId) {
        return accountConsultationUseCase.findAllByUserConnected(userId);
    }

}
