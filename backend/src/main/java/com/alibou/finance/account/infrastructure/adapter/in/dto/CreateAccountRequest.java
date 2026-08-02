package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.AccountType;
import com.alibou.finance.account.domain.vo.AccountTypeCode;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.customer.domain.vo.CustomerId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAccountRequest(
        @NotBlank(message="Type de compte obligatoire")
        String accountTypeCode,//10=>COURANTE|20=>ÉPARGNE|30=>BUSINESS
        String currencyCode,
        @NotNull(message="Client ID est obligatoire")
        UUID customerId
) {

    public static Account toDomain(CreateAccountRequest request){
        CurrencyCode currencyCode = new CurrencyCode(request.currencyCode());
        return Account.builder()
                .accountType(
                        new AccountType(new AccountTypeCode(request.accountTypeCode()))
                )
                .currency(new Currency(currencyCode))
                .customerId(new CustomerId(request.customerId()))
                .build();
    }
}
