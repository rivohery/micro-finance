package com.alibou.finance.account.application.port.dto.command;

import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.account.application.port.dto.vo.ChangedBy;
import com.alibou.finance.log.domain.vo.accountStatusHistory.Reason;
import com.alibou.finance.shared.domain.Assert;
import lombok.Builder;

@Builder
public record AccountLifeCycleCommand(
        AccountId accountId,
        ChangedBy changedBy,
        Reason reason
) {
    public AccountLifeCycleCommand{
        Assert.notNull("accountId", accountId);
        Assert.notNull("changedBy", changedBy);
        Assert.notNull("reason", reason);
    }
}


