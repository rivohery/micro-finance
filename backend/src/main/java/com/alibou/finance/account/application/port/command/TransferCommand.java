package com.alibou.finance.account.application.port.command;

import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.account.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.shared.domain.validator.Assert;
import com.alibou.finance.shared.domain.vo.Description;
import lombok.Builder;

@Builder
public record TransferCommand(
        AccountNumber sourceAccountNumber,
        AccountNumber targetAccountNumber,
        OriginalAmount originalAmount,
        Description description,
        UserInfo userInfo

) {
    public TransferCommand {
        Assert.notNull("sourceAccountNumber", sourceAccountNumber);
        Assert.notNull("targetAccountNumber", targetAccountNumber);
        Assert.notNull("originalAmount", originalAmount);
        Assert.notNull("description", description);
        Assert.notNull("userInfo", userInfo);
    }
}
