package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.account.application.port.usecase.CreateNewAccountUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateNewAccountUseCaseProxy {

    private final CreateNewAccountUseCase createNewAccountUseCase;

    @Transactional
    public Account execute(Account account) {
        return createNewAccountUseCase.execute(account);
    }
}
