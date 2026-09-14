package com.alibou.finance.accountType.application.service;

import com.alibou.finance.accountType.application.port.AccountTypeUseCase;
import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.domain.exception.AccountTypeNotFoundException;
import com.alibou.finance.accountType.domain.repository.AccountTypeRepository;
import com.alibou.finance.accountType.domain.vo.AccountTypeCode;
import com.alibou.finance.accountType.domain.vo.AccountTypeId;
import com.alibou.finance.shared.domain.ObjectInvalidException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class AccountTypeServiceApplication implements AccountTypeUseCase {

    private final AccountTypeRepository accountTypeRepository;

    @Override
    public AccountType findByCode(AccountTypeCode code) {
        return accountTypeRepository.findByCode(code).orElseThrow(
                () -> new AccountTypeNotFoundException("Type de compte introuvable")
        );
    }

    @Override
    public List<AccountType> findAll() {
        return accountTypeRepository.findAll();
    }

    @Override
    public AccountTypeId deleteById(AccountTypeId accountTypeId) {
        boolean accountTypeExist = accountTypeRepository.existsById(accountTypeId);
        if(!accountTypeExist){
            throw new ObjectInvalidException("Identifiant accountTypeId invalide");
        }
        return accountTypeRepository.deleteById(accountTypeId);
    }

    @Override
    public AccountType create(AccountType accountType) {
        accountType.buildAccountTypeIdFrom(UUID.randomUUID());
        return accountTypeRepository.save(accountType);
    }

    @Override
    public AccountType update(AccountType accountType) {
        return accountTypeRepository.save(accountType);
    }

    @Override
    public AccountType findById(AccountTypeId accountTypeId) {
        return accountTypeRepository.findById(accountTypeId).orElseThrow(
                () -> new AccountTypeNotFoundException("Type de compte inexistant")
        );
    }
}
