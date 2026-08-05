package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.account.application.port.dto.input.TransactionInput;
import com.alibou.finance.account.application.port.usecase.AccountTransactionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountTransactionUseCaseProxy {

    private final AccountTransactionUseCase accountTransactionUseCase;

    @Transactional
    public Map<String, Object> deposit(TransactionInput input) {
        return accountTransactionUseCase.deposit(input);
    }

    @Transactional
    public Map<String, Object> withdraw(TransactionInput input) {
        return accountTransactionUseCase.withdraw(input);
    }

    @Transactional
    public Map<String, Object> transfert(TransactionInput input) {
        return accountTransactionUseCase.transfert(input);
    }

}
