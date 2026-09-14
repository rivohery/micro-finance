package com.alibou.finance.account.application.port.dto.command;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.log.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.log.domain.vo.transaction.TransactionCurrencyCode;
import com.alibou.finance.shared.domain.Assert;
import com.alibou.finance.shared.vo.domain.Description;
import lombok.Builder;

@Builder
public record DepositCommand(
        AccountNumber accountNumber,
        OriginalAmount originalAmount,
        TransactionCurrencyCode transactionCurrencyCode,
        Description description,
        User user
) {
    public DepositCommand{
        Assert.notNull("accountNumber", accountNumber);
        Assert.notNull("originalAmount", originalAmount);
        Assert.notNull("transactionCurrencyCode", transactionCurrencyCode);
        Assert.notNull("description", description);
        Assert.notNull("User", user);
    }
}
