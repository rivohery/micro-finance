package com.alibou.finance.accountType.infrastructure.adapter.in.dto;

import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountTypeResponse {
    private UUID id;
    private String name;
    private String code;
    private BigDecimal accountFee;
    private BigDecimal interestRate;
    private BigDecimal minimumBalance;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;

    public static AccountTypeResponse from(AccountType accountType){
        return AccountTypeResponse.builder()
                .id(accountType.getAccountTypeId().value())
                .name(accountType.getName().value())
                .code(accountType.getCode().value())
                .accountFee(accountType.getAccountFee().value())
                .interestRate(accountType.getAnnualInterestRate().value())
                .minimumBalance(accountType.getMinimumBalance().value())
                .createdDate(accountType.getCreatedDate())
                .lastModifiedDate(accountType.getLastModifiedDate())
                .build();
    }
}
