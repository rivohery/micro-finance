package com.alibou.finance.accountType.infrastructure.transactional;

import com.alibou.finance.accountType.application.port.AccountTypeUseCase;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.domain.vo.AccountTypeCode;
import com.alibou.finance.accountType.domain.vo.AccountTypeId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTypeUseCaseProxy {

    private final AccountTypeUseCase accountTypeUseCase;

    @Transactional
    public AccountType create(AccountType accountType){
        return accountTypeUseCase.create(accountType);
    }

    @Transactional
    public AccountType update(AccountType accountType){
        return accountTypeUseCase.update(accountType);
    }

    @Transactional(readOnly = true)
    public AccountType findByCode(AccountTypeCode accountTypeCode){
        return accountTypeUseCase.findByCode(accountTypeCode);
    }

    @Transactional(readOnly = true)
    public List<AccountType> findAll(){
        return accountTypeUseCase.findAll();
    }

    @Transactional
    public AccountTypeId deleteById(AccountTypeId accountTypeId){
        return accountTypeUseCase.deleteById(accountTypeId);
    }

    @Transactional(readOnly = true)
    public AccountType findById(AccountTypeId accountTypeId){
        return accountTypeUseCase.findById(accountTypeId);
    }
}
