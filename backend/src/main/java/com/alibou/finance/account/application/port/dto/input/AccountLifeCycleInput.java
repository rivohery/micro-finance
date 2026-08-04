package com.alibou.finance.account.application.port.dto.input;

import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.account.application.port.dto.vo.ChangedBy;
import com.alibou.finance.log.domain.vo.accountStatusHistory.Reason;
import lombok.Builder;

@Builder
public record AccountLifeCycleInput(AccountId accountId, ChangedBy changedBy, Reason reason) {
}


