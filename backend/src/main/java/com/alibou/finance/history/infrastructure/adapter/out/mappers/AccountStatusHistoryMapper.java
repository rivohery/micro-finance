package com.alibou.finance.history.infrastructure.adapter.out.mappers;

import com.alibou.finance.history.domain.vo.accountStatusHistory.*;
import com.alibou.finance.history.infrastructure.adapter.in.dto.AccountStatusHistoryResponse;
import com.alibou.finance.history.infrastructure.adapter.out.persistence.entity.AccountStatusHistoryEntity;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.history.domain.agregate.AccountStatusHistory;
import com.alibou.finance.shared.domain.vo.history.Reason;

public class AccountStatusHistoryMapper {

    public static AccountStatusHistory entityToDomain(AccountStatusHistoryEntity entity){
        return AccountStatusHistory.builder()
                .accountStatusHistoryId(new AccountStatusHistoryId(entity.getId()))
                .accountId(new AccountId(entity.getAccountId()))
                .newStatus(new NewStatus(entity.getNewStatus()))
                .oldStatus(new OldStatus(entity.getOldStatus()))
                .doingAt(new DoingAt(entity.getDoingAt()))
                .doingBy(new DoingBy(entity.getDoingBy()))
                .reason(new Reason(entity.getReason()))
                .build();
    }

    public static AccountStatusHistoryEntity domainToEntity(AccountStatusHistory domain){
        return AccountStatusHistoryEntity.builder()
                .id(domain.getAccountStatusHistoryId().value())
                .accountId(domain.getAccountId().value())
                .newStatus(domain.getNewStatus().value())
                .oldStatus(domain.getOldStatus().value())
                .doingAt(domain.getDoingAt().value())
                .doingBy(domain.getDoingBy().value())
                .reason(domain.getReason().value())
                .build();
    }

    public static AccountStatusHistoryResponse domainToResponse(AccountStatusHistory domain){
        return AccountStatusHistoryResponse.builder()
                .id(domain.getAccountStatusHistoryId().value())
                .accountId(domain.getAccountId().value())
                .newStatus(domain.getNewStatus().value())
                .oldStatus(domain.getOldStatus().value())
                .doingAt(domain.getDoingAt().value())
                .doingBy(domain.getDoingBy().value())
                .reason(domain.getReason().value())
                .build();
    }
}
