package com.alibou.finance.accountType.internal;

import com.alibou.finance.accountType.AccountTypeInfo;
import com.alibou.finance.accountType.AccountTypeProvider;
import com.alibou.finance.accountType.application.port.AccountTypeUseCase;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountTypeProviderImpl implements AccountTypeProvider {

    private final AccountTypeUseCase accountTypeUseCase;
    @Override
    public AccountTypeInfo findAccountTypeByCode(AccountTypeCode accountTypeCode) {
        AccountType accountType =  accountTypeUseCase.findByCode(accountTypeCode);
        return new AccountTypeInfo(
                accountType.getAccountTypeId(),
                accountType.getCode(),
                accountType.getName(),
                accountType.getAnnualInterestRate(),
                accountType.getMinimumBalance()
        );
    }
}
