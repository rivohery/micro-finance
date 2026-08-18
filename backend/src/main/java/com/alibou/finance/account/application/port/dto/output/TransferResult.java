package com.alibou.finance.account.application.port.dto.output;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.shared.domain.Assert;
import lombok.Builder;

@Builder
public record TransferResult(
        Account sourceAccount,
        Account targetAccount,
        Transaction withdrawTransaction,
        Transaction depositTransaction
) {
    public TransferResult{
        Assert.notNull("sourceAccount", sourceAccount);
        Assert.notNull("targetAccount", targetAccount);
        Assert.notNull("withdrawTransaction", withdrawTransaction);
        Assert.notNull("depositTransaction", depositTransaction);
    }
}
