package com.alibou.finance.log.infrastructure.out.persistence.entity;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name="account_status_histories")
public class AccountStatusHistoryEntity {
    @Id
    UUID id;
    @Column(nullable = false)
    UUID accountId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    AccountStatusEnum oldStatus;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    AccountStatusEnum newStatus;
    @Column(nullable = false)
    String doingBy;
    @Column(nullable = false)
    LocalDateTime doingAt;
    @Column(nullable = false)
    String reason;
}
