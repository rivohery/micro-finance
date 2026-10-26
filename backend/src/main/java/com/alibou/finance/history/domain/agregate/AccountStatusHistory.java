package com.alibou.finance.history.domain.agregate;

import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.history.domain.vo.accountStatusHistory.*;
import com.alibou.finance.shared.domain.vo.history.Reason;
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
