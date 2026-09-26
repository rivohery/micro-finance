package com.alibou.finance.account.application.port.command;

import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.shared.domain.vo.history.Reason;
import com.alibou.finance.shared.domain.validator.Assert;
import lombok.Builder;

@Builder
public record AccountLifeCycleCommand(
        AccountId accountId,
        UserInfo userInfo,//Info sur employee
        Reason reason
) {
    public AccountLifeCycleCommand{
        Assert.notNull("accountId", accountId);
        Assert.notNull("userInfo", userInfo);
        Assert.notNull("reason", reason);
    }
}


