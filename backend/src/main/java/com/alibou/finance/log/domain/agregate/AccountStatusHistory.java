package com.alibou.finance.log.domain.agregate;

import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.log.domain.vo.*;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccountStatusHistory {
    AccountStatusHistoryId accountStatusHistoryId;
    AccountId accountId;
    OldStatus oldStatus;
    NewStatus newStatus;
    DoingBy doingBy;
    DoingAt doingAt;
    Reason reason;
}
