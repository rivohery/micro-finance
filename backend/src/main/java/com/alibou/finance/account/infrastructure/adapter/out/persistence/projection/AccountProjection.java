package com.alibou.finance.account.infrastructure.adapter.out.persistence.projection;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface AccountProjection {
    UUID getId();
    BigDecimal getBalance();
    String getAccountTypeName();
    LocalDate getCreatedDate();
    LocalDate getLastModifiedDate();
    String getAccountNumber();
    AccountStatusEnum getStatus();
    UUID getCustomerId();
    String getCurrencyCode();

}

