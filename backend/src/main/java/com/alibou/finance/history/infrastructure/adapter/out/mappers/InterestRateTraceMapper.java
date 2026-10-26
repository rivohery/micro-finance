package com.alibou.finance.history.infrastructure.adapter.out.mappers;

import com.alibou.finance.history.domain.agregate.InterestRateTrace;
import com.alibou.finance.history.domain.vo.interestRateTrace.MgaExchangeRate;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.history.domain.vo.interestRateTrace.Amount;
import com.alibou.finance.history.domain.vo.interestRateTrace.InterestRateTraceId;
import com.alibou.finance.history.domain.vo.interestRateTrace.MgaAmount;
import com.alibou.finance.history.infrastructure.adapter.in.dto.InterestRateTraceResponse;
import com.alibou.finance.history.infrastructure.adapter.out.persistence.entity.InterestRateTraceEntity;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;

public class InterestRateTraceMapper {

    public static InterestRateTrace entityToDomain(InterestRateTraceEntity entity){
        return InterestRateTrace.builder()
                .interestRateTraceId(new InterestRateTraceId(entity.getId()))
                .accountNumber(new AccountNumber(entity.getAccountNumber()))
                .interestRate(new InterestRate(entity.getInterestRate()))
                .currencyCode(new CurrencyCode(entity.getCurrencyCode()))
                .amount(new Amount(entity.getAmount()))
                .mgaAmount(new MgaAmount(entity.getMgaAmount()))
                .mgaExchangeRate(new MgaExchangeRate(entity.getMgaExchangeRate()))
                .month(entity.getMonth())
                .year(entity.getYear())
                .build();
    }

    public static InterestRateTraceEntity domainToEntity(InterestRateTrace domain){
        return InterestRateTraceEntity.builder()
                .accountNumber(domain.getAccountNumber().value())
                .interestRate(domain.getInterestRate().value())
                .currencyCode(domain.getCurrencyCode().value())
                .amount(domain.getAmount().value())
                .id(domain.getInterestRateTraceId().value())
                .mgaAmount(domain.getMgaAmount().value())
                .mgaExchangeRate(domain.getMgaExchangeRate().value())
                .month(domain.getMonth())
                .year(domain.getYear())
                .build();
    }

    public static InterestRateTraceResponse domainToResponse(InterestRateTrace domain){
        return InterestRateTraceResponse.builder()
                .id(domain.getInterestRateTraceId().value())
                .accountNumber(domain.getAccountNumber().value())
                .interestRate(domain.getInterestRate().value())
                .currencyCode(domain.getCurrencyCode().value())
                .amount(domain.getAmount().value())
                .mgaAmount(domain.getMgaAmount().value())
                .mgaExchangeRate(domain.getMgaExchangeRate().value())
                .month(domain.getMonth())
                .year(domain.getYear())
                .build();
    }
}
