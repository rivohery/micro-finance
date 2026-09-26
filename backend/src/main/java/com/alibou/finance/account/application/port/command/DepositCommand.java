package com.alibou.finance.account.application.port.command;

import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.account.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.account.domain.vo.transaction.TransactionCurrencyCode;
import com.alibou.finance.shared.domain.validator.Assert;
import com.alibou.finance.shared.domain.vo.Description;
import com.alibou.finance.shared.domain.vo.user.Username;
import lombok.Builder;

@Builder
public record DepositCommand(
        AccountNumber accountNumber,
        OriginalAmount originalAmount,
        TransactionCurrencyCode transactionCurrencyCode,
        Description description,
        Username username
) {
    public DepositCommand{
        Assert.notNull("accountNumber", accountNumber);
        Assert.notNull("originalAmount", originalAmount);
        Assert.notNull("transactionCurrencyCode", transactionCurrencyCode);
        Assert.notNull("description", description);
        Assert.notNull("Username", username);
    }
}
