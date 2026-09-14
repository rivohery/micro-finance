package com.alibou.finance.currency.infrastructure.adapter.out.mapper;

import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.domain.vo.CurrencyId;
import com.alibou.finance.currency.domain.vo.CurrencyName;
import com.alibou.finance.currency.infrastructure.adapter.out.entity.CurrencyEntity;

public class CurrencyMapper {
    public static Currency entityToDomain(CurrencyEntity entity){
        return Currency.builder()
                .currencyId(CurrencyId.from(entity.getId()))
                .code(new CurrencyCode(entity.getCode()))
                .name(new CurrencyName(entity.getName()))
                .enable(entity.isEnable())
                .build();
    }

    public static CurrencyEntity domainToEntity(Currency domain){
        return CurrencyEntity.builder()
                .id(domain.getCurrencyId().value())
                .code(domain.getCode().value())
                .name(domain.getName().value())
                .enable(domain.isEnable())
                .build();
    }
}
