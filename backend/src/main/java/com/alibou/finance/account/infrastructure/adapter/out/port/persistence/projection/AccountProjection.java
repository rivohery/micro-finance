package com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection;

import com.alibou.finance.shared.domain.enums.AccountStatusEnum;

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

