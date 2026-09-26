package com.alibou.finance.account.infrastructure.adapter.out.mapper;

import com.alibou.finance.account.application.port.command.AccountLifeCycleCommand;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.vo.account.AccountStatus;
import com.alibou.finance.account.domain.vo.account.Balance;
import com.alibou.finance.account.domain.vo.account.MgaBalance;
import com.alibou.finance.account.infrastructure.adapter.in.dto.AccountLifeCycleRequest;
import com.alibou.finance.account.infrastructure.adapter.in.dto.AccountResponse;
import com.alibou.finance.account.domain.vo.account.OverdraftLimit;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.AccountProjection;
import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.domain.vo.history.Reason;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeName;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.currency.CurrencyId;

public class AccountMapper {


    public static AccountLifeCycleCommand buildAccountLifeCycleCommand(UserInfo userInfo, AccountLifeCycleRequest request) {
        return AccountLifeCycleCommand.builder()
                .accountId(AccountId.from(request.accountId()))
                .userInfo(userInfo)
                .reason(new Reason(request.reason()))
                .build();
    }


    public static Account entityToDomain(AccountEntity entity) {

        return Account.builder()
                .accountId(AccountId.from(entity.getId()))
                .customerId(new CustomerId(entity.getCustomerId()))
                .currencyId(new CurrencyId(entity.getCurrencyId()))
                .currencyCode(new CurrencyCode(entity.getCurrencyCode()))
                .accountTypeId(new AccountTypeId(entity.getAccountTypeId()))
                .accountTypeName(new AccountTypeName(entity.getAccountTypeName()))
                .accountTypeCode(new AccountTypeCode(entity.getAccountTypeCode()))
                .annualInterestRate(new InterestRate(entity.getAnnualInterestRate()))
                .accountNumber(new AccountNumber(entity.getAccountNumber()))
                .accountStatus(new AccountStatus(entity.getAccountStatus()))
                .balance(new Balance(entity.getBalance()))
                .mgaBalance(new MgaBalance(entity.getMgaBalance()))
                .overdraftLimit(new OverdraftLimit(entity.getOverdraftLimit()))
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .build();
    }

    public static AccountEntity domainToEntity(Account account) {
        return AccountEntity.builder()
                .accountNumber(account.getAccountNumber().value())
                .accountStatus(account.getAccountStatus().value())
                .overdraftLimit(account.getOverdraftLimit().value())
                .accountTypeId(account.getAccountTypeId().value())
                .accountTypeName(account.getAccountTypeName().value())
                .accountTypeCode(account.getAccountTypeCode().value())
                .annualInterestRate(account.getAnnualInterestRate().value())
                .balance(account.getBalance().value())
                .mgaBalance(account.getMgaBalance().value())
                .currencyId(account.getCurrencyId().value())
                .currencyCode(account.getCurrencyCode().value())
                .customerId(account.getCustomerId().value())
                .id(account.getAccountId().value())
                .build();
    }

    public static AccountEntity updateEntityFromDomain(Account account, AccountEntity accountEntity) {
        accountEntity.setAccountStatus(account.getAccountStatus().value());
        accountEntity.setOverdraftLimit(account.getOverdraftLimit().value());
        accountEntity.setBalance(account.getBalance().value());
        accountEntity.setMgaBalance(account.getMgaBalance().value());
        accountEntity.setAnnualInterestRate(account.getAnnualInterestRate().value());
        return accountEntity;
    }

    public static Account domainFromProjection(AccountProjection projection) {
        return Account.builder()
                .accountId(AccountId.from(projection.getId()))
                .customerId(new CustomerId(projection.getCustomerId()))
                .accountTypeName(new AccountTypeName(projection.getAccountTypeName()))
                .currencyCode(new CurrencyCode(projection.getCurrencyCode()))
                .accountNumber(new AccountNumber(projection.getAccountNumber()))
                .accountStatus(new AccountStatus(projection.getStatus()))
                .balance(new Balance(projection.getBalance()))
                .createdDate(projection.getCreatedDate())
                .lastModifiedDate(projection.getLastModifiedDate())
                .build();
    }

    public static AccountResponse domainToAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getAccountId().value())
                .balance(account.getBalance().value())
                .currencyCode(account.getCurrencyCode().value())
                .accountNumber(account.getAccountNumber().value())
                .accountType(account.getAccountTypeName().value())
                .createdDate(account.getCreatedDate())
                .lastModifiedDate(account.getLastModifiedDate())
                .customerId(account.getCustomerId().value())
                .status(account.getAccountStatus().value())
                .build();
    }
}
