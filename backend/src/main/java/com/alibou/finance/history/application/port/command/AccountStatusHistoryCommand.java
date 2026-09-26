package com.alibou.finance.history.application.port.command;

import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.history.domain.vo.accountStatusHistory.*;
import com.alibou.finance.shared.domain.validator.Assert;
import com.alibou.finance.shared.domain.vo.history.Reason;
import lombok.Builder;

@Builder
public record AccountStatusHistoryCommand(
      AccountId accountId,
      DoingBy doingBy,
      Reason reason,
      OldStatus oldStatus,
      NewStatus newStatus
) {

    public AccountStatusHistoryCommand{
        Assert.notNull("accountId", accountId);
        Assert.notNull("doingBy", doingBy);
        Assert.notNull("reason", reason);
        Assert.notNull("oldStatus", oldStatus);
        Assert.notNull("newStatus", newStatus);
    }

}
