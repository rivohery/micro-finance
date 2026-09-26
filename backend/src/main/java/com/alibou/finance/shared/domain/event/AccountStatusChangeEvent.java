package com.alibou.finance.shared.domain.event;

import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.shared.domain.vo.history.Reason;
import com.alibou.finance.shared.domain.vo.user.Username;

public record AccountStatusChangeEvent(
        AccountId accountId,
        Username username, //username de l'employé
        AccountStatusEnum oldStatus,
        AccountStatusEnum newStatus,
        Reason reason
) {
}
