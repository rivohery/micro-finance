package com.alibou.finance.account.application.port.result;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.shared.domain.validator.Assert;
import lombok.Builder;

@Builder
public record TransactionResult(
        Account account,
        Transaction transaction
) {
    public TransactionResult{
        Assert.notNull("account", account);
        Assert.notNull("transaction", transaction);
    }
}
