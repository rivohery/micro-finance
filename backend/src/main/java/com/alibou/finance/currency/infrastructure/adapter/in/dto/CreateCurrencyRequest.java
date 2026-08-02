package com.alibou.finance.currency.infrastructure.adapter.in.dto;

import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyName;
import jakarta.validation.constraints.NotBlank;

public record CreateCurrencyRequest(
         @NotBlank
         String name,
         @NotBlank
         String code
) {
    public static Currency toDomain(CreateCurrencyRequest request){
        return Currency.builder()
                .name(new CurrencyName(request.name()))
                .code(new CurrencyCode(request.code()))
                .build();
    }
}
