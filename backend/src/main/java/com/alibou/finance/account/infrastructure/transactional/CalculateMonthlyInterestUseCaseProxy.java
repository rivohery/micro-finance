package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.account.application.port.usecase.CalculateMonthlyInterestUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalculateMonthlyInterestUseCaseProxy {

    private final CalculateMonthlyInterestUseCase calculateMonthlyInterestUseCase;

    @Transactional
    public Account execute(Account account) {
        return calculateMonthlyInterestUseCase.execute(account);
    }

}
