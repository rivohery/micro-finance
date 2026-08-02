package com.alibou.finance.log.infrastructure.out.mappers;

import com.alibou.finance.log.infrastructure.in.dto.AccountStatusHistoryResponse;
import com.alibou.finance.log.infrastructure.out.persistence.entity.AccountStatusHistoryEntity;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.domain.vo.*;

public class AccountStatusHistoryMapper {

    public static AccountStatusHistory entityToDomain(AccountStatusHistoryEntity entity){
        return AccountStatusHistory.builder()
                .accountStatusHistoryId(AccountStatusHistoryId.from(entity.getId()))
                .accountId(AccountId.from(entity.getAccountId()))
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
