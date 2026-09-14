package com.alibou.finance.currency.infrastructure.adapter.in.dto;

import com.alibou.finance.currency.domain.agregate.Currency;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyResponse {
    private UUID id;
    private String name;
    private String code;
    private boolean enable;

    public static CurrencyResponse fromDomain(Currency currency){
        return CurrencyResponse.builder()
                .id(currency.getCurrencyId().value())
                .name(currency.getName().value())
                .code(currency.getCode().value())
                .enable(currency.isEnable())
                .build();
    }
}
