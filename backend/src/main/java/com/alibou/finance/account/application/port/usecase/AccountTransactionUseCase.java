package com.alibou.finance.account.application.port.usecase;

import com.alibou.finance.account.application.port.dto.input.TransactionInput;

import java.util.Map;

public interface AccountTransactionUseCase {

    Map<String, Object> deposit(TransactionInput input);

    Map<String, Object> withdraw(TransactionInput input);

    Map<String, Object> transfert(TransactionInput input);
}
