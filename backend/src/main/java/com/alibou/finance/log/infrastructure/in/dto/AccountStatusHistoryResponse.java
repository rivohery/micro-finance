package com.alibou.finance.log.infrastructure.in.dto;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountStatusHistoryResponse {
    UUID id;
    UUID accountId;
    AccountStatusEnum oldStatus;
    AccountStatusEnum newStatus;
    String doingBy;
    LocalDateTime doingAt;
    String reason;
}
