package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.account.application.port.usecase.AddMonthlyInterestUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddMonthlyInterestUseCaseProxy {

    private final AddMonthlyInterestUseCase addMonthlyInterestUseCase;

    @Transactional
    public Account execute(Account account) {
        return addMonthlyInterestUseCase.execute(account);
    }

}
