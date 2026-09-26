package com.alibou.finance.accountType.infrastructure.transactional;

import com.alibou.finance.accountType.application.command.AccountTypeCommand;
import com.alibou.finance.accountType.application.port.AccountTypeUseCase;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTypeUseCaseProxy {

    private final AccountTypeUseCase accountTypeUseCase;

    @Transactional
    public AccountType create(AccountTypeCommand accountTypeCommand){
        return accountTypeUseCase.create(accountTypeCommand);
    }

    @Transactional
    public AccountType update(AccountTypeCommand accountTypeCommand, AccountTypeId accountTypeId){
        return accountTypeUseCase.update(accountTypeCommand, accountTypeId);
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
    public void deleteById(AccountTypeId accountTypeId){
        accountTypeUseCase.deleteById(accountTypeId);
    }

    @Transactional(readOnly = true)
    public AccountType findById(AccountTypeId accountTypeId){
        return accountTypeUseCase.findById(accountTypeId);
    }
}
