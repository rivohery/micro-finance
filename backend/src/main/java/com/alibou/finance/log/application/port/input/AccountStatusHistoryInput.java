package com.alibou.finance.log.application.port.input;

import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.domain.vo.accountStatusHistory.*;
import lombok.Builder;

@Builder
public record AccountStatusHistoryInput(
      AccountId accountId,
      DoingBy doingBy,
      Reason reason,
      OldStatus oldStatus,
      NewStatus newStatus
) {

    public static AccountStatusHistory toAgregate(AccountStatusHistoryInput input){
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
