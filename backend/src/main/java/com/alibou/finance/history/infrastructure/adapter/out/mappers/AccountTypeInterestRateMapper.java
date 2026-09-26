package com.alibou.finance.history.infrastructure.adapter.out.mappers;

import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import com.alibou.finance.history.domain.agregate.AccountTypeInterestRate;
import com.alibou.finance.history.domain.vo.accountTypeRate.AccountTypeRateId;
import com.alibou.finance.history.domain.vo.accountTypeRate.Annee;
import com.alibou.finance.history.infrastructure.adapter.out.persistence.entity.AccountTypeInterestRateEntity;

public class AccountTypeInterestRateMapper {

    public static AccountTypeInterestRate entityToDomain(AccountTypeInterestRateEntity entity){
        return AccountTypeInterestRate.builder()
                .accountTypeRateId(new AccountTypeRateId(entity.getId()))
                .accountTypeId(new AccountTypeId(entity.getAccountTypeId()))
                .accountTypeCode(new AccountTypeCode(entity.getAccountTypeCode()))
                .annualInterestRate(new InterestRate(entity.getAnnualInterestRate()))
                .annee(new Annee(entity.getAnnee()))
                .build();
    }

    public static AccountTypeInterestRateEntity domainToEntity(AccountTypeInterestRate domain){
        return AccountTypeInterestRateEntity.builder()
                .id(domain.getAccountTypeRateId().value())
                .accountTypeId(domain.getAccountTypeId().value())
                .accountTypeCode(domain.getAccountTypeCode().value())
                .annualInterestRate(domain.getAnnualInterestRate().value())
                .annee(domain.getAnnee().value())
                .build();
    }
}
