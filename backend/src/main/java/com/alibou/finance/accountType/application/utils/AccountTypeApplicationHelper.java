package com.alibou.finance.accountType.application.utils;

import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.domain.exception.AccountTypeNotFoundException;
import com.alibou.finance.accountType.domain.out.event.AccountTypeEventPublisher;
import com.alibou.finance.accountType.domain.out.repository.AccountTypeRepository;
import com.alibou.finance.shared.domain.event.AccountTypeSavedEvent;
import com.alibou.finance.shared.domain.event.AnnualInterestRateChangeEvent;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.exception.DuplicateValueException;
import com.alibou.finance.shared.domain.exception.ObjectInvalidException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountTypeApplicationHelper {

    private final AccountTypeRepository accountTypeRepository;
    private final AccountTypeEventPublisher accountTypeEventPublisher;

    public AccountType getAccountTypeByCode(AccountTypeCode code){
        return accountTypeRepository.findByCode(code).orElseThrow(
                () -> new AccountTypeNotFoundException("Type de compte introuvable")
        );
    }

    public void verifyIfAccountTypeExist(AccountTypeId accountTypeId){
        if(!accountTypeRepository.existsById(accountTypeId)){
            throw new ObjectInvalidException("Identifiant accountTypeId invalide");
        }
    }

    public void verifyUniquenessCode(AccountTypeCode accountTypeCode){
        if(accountTypeRepository.existsByCode(accountTypeCode)){
            throw new DuplicateValueException("Ce code est deja utilisé par un autre type");
        }
    }

    public AccountType getAccountTypeById(AccountTypeId accountTypeId){
        return accountTypeRepository.findById(accountTypeId).orElseThrow(
                () -> new AccountTypeNotFoundException("Type de compte inexistant")
        );
    }

    public void publishAccountTypeSavedEvent(AccountType saved){
        AccountTypeSavedEvent accountTypeSavedEvent = new AccountTypeSavedEvent(
                saved.getAccountTypeId(),
                saved.getCode(),
                saved.getAnnualInterestRate()
        );
        accountTypeEventPublisher.publish(accountTypeSavedEvent);
    }

    public void publishAnnualInterestRateChangeEvent(AccountType updated){
        AnnualInterestRateChangeEvent annualInterestRateChangeEvent = new AnnualInterestRateChangeEvent(
                updated.getAccountTypeId(),
                updated.getAnnualInterestRate()
        );
        accountTypeEventPublisher.publish(annualInterestRateChangeEvent);
    }
}
