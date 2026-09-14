package com.alibou.finance.currency.infrastructure.adapter.in.dto;

import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyId;
import com.alibou.finance.currency.domain.vo.CurrencyName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCurrencyRequest(
        @NotNull
        UUID id,
        @NotBlank
        String name,
        @NotBlank
        String code,
        @NotNull
        boolean enable
) {
    public static Currency toDomain(UpdateCurrencyRequest request){
       return Currency.builder()
               .currencyId(CurrencyId.from(request.id()))
               .name(new CurrencyName(request.name()))
               .code(new CurrencyCode(request.code()))
               .enable(request.enable())
               .build();
    }
}
