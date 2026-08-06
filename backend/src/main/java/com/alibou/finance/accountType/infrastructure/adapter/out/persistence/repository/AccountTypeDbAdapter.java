package com.alibou.finance.accountType.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.domain.repository.AccountTypeRepository;
import com.alibou.finance.accountType.domain.vo.AccountTypeCode;
import com.alibou.finance.accountType.domain.vo.AccountTypeId;
import com.alibou.finance.accountType.infrastructure.adapter.out.mapper.AccountTypeMapper;
import com.alibou.finance.accountType.infrastructure.adapter.out.persistence.entity.AccountTypeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountTypeDbAdapter implements AccountTypeRepository {

    private final AccountTypeJpaRepository accountTypeJpaRepository;

    @Override
    public AccountType save(AccountType accountType) {
        AccountTypeEntity entity = AccountTypeMapper.domainToEntity(accountType);
        return AccountTypeMapper.entityToDomain(accountTypeJpaRepository.save(entity));
    }

    @Override
    public Optional<AccountType> findByCode(AccountTypeCode code) {
        return accountTypeJpaRepository.findByCode(code.value()).map(AccountTypeMapper::entityToDomain);
    }

    @Override
    public boolean existsById(AccountTypeId accountTypeId) {
        return accountTypeJpaRepository.existsById(accountTypeId.value());
    }

    @Override
    public List<AccountType> findAll() {
        return accountTypeJpaRepository.findAll()
                .stream()
                .map(AccountTypeMapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<AccountType> findById(AccountTypeId accountTypeId) {
        return accountTypeJpaRepository.findById(accountTypeId.value()).map(AccountTypeMapper::entityToDomain);
    }

    @Override
    public AccountTypeId deleteById(AccountTypeId accountTypeId) {
        try{
            accountTypeJpaRepository.deleteById(accountTypeId.value());
            return accountTypeId;
        }catch (Exception ex){
            throw new RuntimeException("La suppression est interrompu.Peut-être,il y a encore des comptes ayant ce type dans BD");
        }
    }

}
