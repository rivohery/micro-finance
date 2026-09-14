package com.alibou.finance.account.application.port.dto.command;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.log.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.shared.domain.Assert;
import com.alibou.finance.shared.vo.domain.Description;
import lombok.Builder;

@Builder
public record TransferCommand(
        AccountNumber sourceAccountNumber,
        AccountNumber targetAccountNumber,
        OriginalAmount originalAmount,
        Description description,
        User user

) {
    public TransferCommand {
        Assert.notNull("sourceAccountNumber", sourceAccountNumber);
        Assert.notNull("targetAccountNumber", targetAccountNumber);
        Assert.notNull("originalAmount", originalAmount);
        Assert.notNull("description", description);
        Assert.notNull("user", user);
    }
}
