package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {
    private UUID id;
    private BigDecimal balance;
    private String accountType;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    private String accountNumber;
    private AccountStatusEnum status;
    private UUID customerId;
    private String currencyCode;

}
