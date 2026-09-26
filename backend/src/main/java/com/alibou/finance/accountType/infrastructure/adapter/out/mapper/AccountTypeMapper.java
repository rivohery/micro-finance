package com.alibou.finance.accountType.infrastructure.adapter.out.mapper;

import com.alibou.finance.accountType.application.command.AccountTypeCommand;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.infrastructure.adapter.in.dto.AccountTypeRequest;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import com.alibou.finance.accountType.domain.vo.*;
import com.alibou.finance.shared.domain.vo.accountType.*;

import java.util.Objects;

public class AccountTypeMapper {

    public static AccountTypeCommand requestToAccountTypeCommand(
            AccountTypeRequest request
    ){
        return new AccountTypeCommand(
                new AccountTypeCode(request.code()),
                new AccountTypeName(request.name()),
                new AccountFee(request.accountFee()),
                new InterestRate(request.interestRate()),
                new MinimumBalance(request.minimumBalance())
        );
    }

    public static AccountTypeEntity domainToEntity(AccountType accountType){
        if(Objects.nonNull(accountType)) {
            return AccountTypeEntity.builder()
                    .id(accountType.getAccountTypeId().value())
                    .name(accountType.getName().value())
                    .accountFee(accountType.getAccountFee().value())
                    .annualInterestRate(accountType.getAnnualInterestRate().value())
                    .minimumBalance(accountType.getMinimumBalance().value())
                    .code(accountType.getCode().value())
                    .build();
        }
        return null;
    }

    public static AccountType entityToDomain(AccountTypeEntity entity){
        if(Objects.nonNull(entity)){
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
        return null;
    }
}
