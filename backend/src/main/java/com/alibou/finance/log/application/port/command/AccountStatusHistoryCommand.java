package com.alibou.finance.log.application.port.command;

import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.domain.vo.accountStatusHistory.*;
import com.alibou.finance.shared.domain.Assert;
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

    public static AccountStatusHistory toAgregate(AccountStatusHistoryCommand input){
        return AccountStatusHistory
                .builder()
                .accountStatusHistoryId(AccountStatusHistoryId.generate())
                .accountId(input.accountId())
                .doingAt(DoingAt.now())
                .doingBy(input.doingBy())
                .reason(input.reason)
                .oldStatus(input.oldStatus)
                .newStatus(input.newStatus)
                .build();
    }
}
