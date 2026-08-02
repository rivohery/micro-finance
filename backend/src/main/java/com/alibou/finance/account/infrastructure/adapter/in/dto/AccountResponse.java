package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
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

    public static AccountResponse fromDomain(Account account){
        return AccountResponse.builder()
                .id(account.getAccountId().value())
                .balance(account.getBalance().value())
                .currencyCode(account.getCurrency().getCode().value())
                .accountNumber(account.getAccountNumber().value())
                .accountType(account.getAccountType().getName().value())
                .createdDate(account.getCreatedDate())
                .lastModifiedDate(account.getLastModifiedDate())
                .customerId(account.getCustomerId().value())
                .status(account.getAccountStatus().value())
                .build();
    }

}
