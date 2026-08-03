package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.usecase.AccountTypeUseCase;
import com.alibou.finance.account.domain.agregate.AccountType;
import com.alibou.finance.account.domain.exception.AccountTypeNotFoundException;
import com.alibou.finance.account.domain.out.repository.AccountTypeRepository;
import com.alibou.finance.account.domain.vo.AccountTypeId;
import com.alibou.finance.shared.domain.ObjectInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountTypeServiceApplication implements AccountTypeUseCase {

    private final AccountTypeRepository accountTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public AccountType findByCode(String code) {
        return accountTypeRepository.findByCode(code).orElseThrow(
                () -> new AccountTypeNotFoundException("Type de compte introuvable")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountType> findAll() {
        return accountTypeRepository.findAll();
    }

    @Override
    @Transactional
    public AccountTypeId deleteById(AccountTypeId accountTypeId) {
        boolean accountTypeExist = accountTypeRepository.existsById(accountTypeId);
        if(!accountTypeExist){
            throw new ObjectInvalidException("Identifiant accountTypeId invalide");
        }
        return accountTypeRepository.deleteById(accountTypeId);
    }

    @Override
    @Transactional
    public AccountType create(AccountType accountType) {
        accountType.buildAccountTypeIdFrom(UUID.randomUUID());
        return accountTypeRepository.save(accountType);
    }

    @Override
    @Transactional
    public AccountType update(AccountType accountType) {
        return accountTypeRepository.save(accountType);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountType findById(AccountTypeId accountTypeId) {
        return accountTypeRepository.findById(accountTypeId).orElseThrow(
                () -> new AccountTypeNotFoundException("Type de compte inexistant")
        );
    }
}
