package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.account.domain.agregate.AccountType;
import com.alibou.finance.account.domain.vo.AccountFee;
import com.alibou.finance.account.domain.vo.AccountTypeCode;
import com.alibou.finance.account.domain.vo.AccountTypeName;
import com.alibou.finance.account.domain.vo.MinimumBalance;
import com.alibou.finance.log.domain.vo.InterestRate;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record AccountTypeRequest(
        @NotBlank(message = "Le nom est requis")
        String name,
        @NotBlank(message = "Le code est requis")//10-20-30
         String code,
        BigDecimal accountFee,
        BigDecimal interestRate,
        BigDecimal minimumBalance
) {

    public static AccountType toDomain(AccountTypeRequest request){
        return AccountType.builder()
                .name(new AccountTypeName(request.name()))
                .code(new AccountTypeCode(request.code()))
                .accountFee(new AccountFee(request.accountFee()))
                .annualInterestRate(new InterestRate(request.interestRate()))
                .minimumBalance(new MinimumBalance(request.minimumBalance()))
                .build();
    }
}
