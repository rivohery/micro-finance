package com.alibou.finance.account.infrastructure.adapter.out.port.linker;

import com.alibou.finance.account.domain.out.linker.AccountTypeLinkerPort;
import com.alibou.finance.accountType.AccountTypeInfo;
import com.alibou.finance.accountType.AccountTypeProvider;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountTypeLinkerPortAdapter implements AccountTypeLinkerPort {

    private final AccountTypeProvider accountTypeProvider;

    @Override
    public AccountTypeInfo getAccountTypeByCode(AccountTypeCode accountTypeCode) {
        return accountTypeProvider.findAccountTypeByCode(accountTypeCode);
    }
}
