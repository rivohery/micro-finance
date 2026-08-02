package com.alibou.finance.account.infrastructure.adapter.out.mapper;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.AccountType;
import com.alibou.finance.account.domain.agregate.OverdraftLimit;
import com.alibou.finance.account.domain.vo.*;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.AccountProjection;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import com.alibou.finance.currency.infrastructure.adapter.out.mapper.CurrencyMapper;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.customer.domain.vo.CustomerId;

public class AccountMapper {

    public static Account entityToDomain(AccountEntity entity){

        return Account.builder()
                .accountId(AccountId.from(entity.getId()))
                .customerId(new CustomerId(entity.getCustomerId()))
                .currency(CurrencyMapper.entityToDomain(entity.getCurrencyEntity()))
                .accountType(AccountTypeMapper.entityToDomain(entity.getAccountTypeEntity()))
                .accountNumber(new AccountNumber(entity.getAccountNumber()))
                .accountStatus(new AccountStatus(entity.getAccountStatus()))
                .balance(new Balance(entity.getBalance()))
                .mgaBalance(new MgaBalance(entity.getMgaBalance()))
                .overdraftLimit(new OverdraftLimit(entity.getOverdraftLimit()))
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .build();
    }

    public static AccountEntity domainToEntity(Account account){
        return AccountEntity.builder()
                .accountNumber(account.getAccountNumber().value())
                .accountStatus(account.getAccountStatus().value())
                .overdraftLimit(account.getOverdraftLimit().value())
                .accountTypeEntity(AccountTypeMapper.domainToEntity(account.getAccountType()))
                .balance(account.getBalance().value())
                .mgaBalance(account.getMgaBalance().value())
                .currencyEntity(CurrencyMapper.domainToEntity(account.getCurrency()))
                .customerId(account.getCustomerId().value())
                .id(account.getAccountId().value())
                .build();
    }

    public static Account domainFromProjection(AccountProjection proj){
        return Account.builder()
                .accountId(AccountId.from(proj.getId()))
                .customerId(new CustomerId(proj.getCustomerId()))
                .currency(new Currency(CurrencyCode.from(proj.getCurrencyCode())))
                .accountType(new AccountType(AccountTypeName.from(proj.getAccountTypeName())))
                .accountNumber(new AccountNumber(proj.getAccountNumber()))
                .accountStatus(new AccountStatus(proj.getStatus()))
                .balance(new Balance(proj.getBalance()))
                .createdDate(proj.getCreatedDate())
                .lastModifiedDate(proj.getLastModifiedDate())
                .build();
    }
}
