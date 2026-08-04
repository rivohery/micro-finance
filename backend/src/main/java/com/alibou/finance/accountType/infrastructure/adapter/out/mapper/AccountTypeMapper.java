package com.alibou.finance.accountType.infrastructure.adapter.out.mapper;

import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import com.alibou.finance.accountType.domain.vo.*;
import com.alibou.finance.log.domain.vo.accountStatusHistory.InterestRate;

public class AccountTypeMapper {

    public static AccountTypeEntity domainToEntity(AccountType accountType){
        return AccountTypeEntity.builder()
                .id(accountType.getAccountTypeId().value())
                .name(accountType.getName().value())
                .accountFee(accountType.getAccountFee().value())
                .annualInterestRate(accountType.getAnnualInterestRate().value())
                .minimumBalance(accountType.getMinimumBalance().value())
                .code(accountType.getCode().value())
                .build();
    }

    public static AccountType entityToDomain(AccountTypeEntity entity){
        return AccountType.builder()
                .accountTypeId(AccountTypeId.from(entity.getId()))
                .name(new AccountTypeName(entity.getName()))
                .code(new AccountTypeCode(entity.getCode()))
                .annualInterestRate(new InterestRate(entity.getAnnualInterestRate()))
                .accountFee(new AccountFee(entity.getAccountFee()))
                .minimumBalance(new MinimumBalance(entity.getMinimumBalance()))
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .build();
    }
}
