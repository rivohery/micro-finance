package com.alibou.finance.accountType.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountTypeRequest(
        @NotBlank(message = "Le nom est requis")
        String name,
        @NotBlank(message = "Le code est requis")//10-20-30
         String code,
        @NotNull
        BigDecimal accountFee,
        @NotNull
        BigDecimal interestRate,
        @NotNull
        BigDecimal minimumBalance
) { }
