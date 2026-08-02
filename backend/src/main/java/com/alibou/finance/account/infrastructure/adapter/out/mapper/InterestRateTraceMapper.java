package com.alibou.finance.account.infrastructure.adapter.out.mapper;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.AccountType;
import com.alibou.finance.account.domain.agregate.InterestRateTrace;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.domain.vo.interestRate.Amount;
import com.alibou.finance.account.domain.vo.interestRate.InterestRateTraceId;
import com.alibou.finance.account.domain.vo.interestRate.MgaAmount;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.InterestRateTraceEntity;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.log.domain.vo.InterestRate;

public class InterestRateTraceMapper {

    public static InterestRateTrace toDomain(InterestRateTraceEntity entity){
        Account account = Account.builder()
                .accountNumber(new AccountNumber(entity.getAccountNumber()))
                .currency(Currency.builder().code(new CurrencyCode(entity.getCurrencyCode())).build())
                .accountType(AccountType.builder().annualInterestRate(new InterestRate(entity.getInterestRate())).build())
                .build();
        return InterestRateTrace.builder()
                .interestRateTraceId(InterestRateTraceId.from(entity.getId()))
                .account(account)
                .amount(new Amount(entity.getAmount()))
                .mgaAmount(new MgaAmount(entity.getMgaAmount()))
                .month(entity.getMonth())
                .year(entity.getYear())
                .build();
    }

    public static InterestRateTraceEntity toEntity(InterestRateTrace domain){
        return InterestRateTraceEntity.builder()
                .accountNumber(domain.getAccount().getAccountNumber().value())
                .interestRate(domain.getAccount().getAccountType().getAnnualInterestRate().value())
                .currencyCode(domain.getAccount().getCurrency().getCode().value())
                .amount(domain.getAmount().value())
                .id(domain.getInterestRateTraceId().value())
                .mgaAmount(domain.getMgaAmount().value())
                .month(domain.getMonth())
                .year(domain.getYear())
                .build();
    }
}
