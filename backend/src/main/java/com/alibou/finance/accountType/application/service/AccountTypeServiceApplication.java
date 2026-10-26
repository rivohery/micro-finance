package com.alibou.finance.accountType.application.service;

import com.alibou.finance.accountType.application.command.AccountTypeCommand;
import com.alibou.finance.accountType.application.port.AccountTypeUseCase;
import com.alibou.finance.accountType.application.utils.AccountTypeApplicationHelper;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.domain.out.repository.AccountTypeRepository;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class AccountTypeServiceApplication implements AccountTypeUseCase {
    private final AccountTypeRepository accountTypeRepository;
    private final AccountTypeApplicationHelper accountTypeApplicationHelper;

    @Override
    public AccountType findByCode(AccountTypeCode code) {
        return accountTypeApplicationHelper.getAccountTypeByCode(code);
    }

    @Override
    public List<AccountType> findAll() {
        return accountTypeRepository.findAll();
    }

    @Override
    public void deleteById(AccountTypeId accountTypeId) {
        accountTypeApplicationHelper.verifyIfAccountTypeExist(accountTypeId);

        accountTypeRepository.deleteById(accountTypeId);

    }

    @Override
    public AccountType create(AccountTypeCommand cmd) {
        accountTypeApplicationHelper.verifyUniquenessCode(cmd.accountTypeCode());
        AccountType newAccountType = AccountType.createFrom(cmd);
        AccountType created =  accountTypeRepository.save(newAccountType);
        if(created != null){
            accountTypeApplicationHelper.publishAccountTypeSavedEvent(created);
            return created;
        }
        return null;
    }


    @Override
    public AccountType update(AccountTypeCommand cmd, AccountTypeId accountTypeId) {
        AccountType actual = accountTypeApplicationHelper.getAccountTypeById(accountTypeId);
        Set<String> updatedField = actual.updateFrom(cmd);
        if(!updatedField.isEmpty()){
            AccountType updated = accountTypeRepository.save(actual);
            if(updated != null && updatedField.contains("annualInterestRate")){
                accountTypeApplicationHelper.publishAccountTypeSavedEvent(updated);
                accountTypeApplicationHelper.publishAnnualInterestRateChangeEvent(updated);
            }
            return updated;
        }
        return actual;
    }

    @Override
    public AccountType findById(AccountTypeId accountTypeId) {
        return accountTypeApplicationHelper.getAccountTypeById(accountTypeId);
    }
}
