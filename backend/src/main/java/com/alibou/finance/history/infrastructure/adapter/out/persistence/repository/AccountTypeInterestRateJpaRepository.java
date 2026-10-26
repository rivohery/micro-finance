package com.alibou.finance.history.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.history.infrastructure.adapter.out.persistence.entity.AccountTypeInterestRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AccountTypeInterestRateJpaRepository extends JpaRepository<AccountTypeInterestRateEntity, UUID> {

    List<AccountTypeInterestRateEntity> findAllByAccountTypeIdIn(Set<AccountTypeId> accountTypeIds);
}
