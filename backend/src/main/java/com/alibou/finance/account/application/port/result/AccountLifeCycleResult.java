package com.alibou.finance.account.application.port.result;
import com.alibou.finance.account.domain.vo.account.AccountStatus;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import lombok.Builder;

@Builder
public record AccountLifeCycleResult(
        AccountId accountId,
        AccountStatus accountStatus
) {
}
