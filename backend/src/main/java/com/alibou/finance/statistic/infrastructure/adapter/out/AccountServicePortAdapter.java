package com.alibou.finance.statistic.infrastructure.adapter.out;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.NumberAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.SoldeAccountStatisticProj;
import com.alibou.finance.statistic.domain.service.AccountServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountServicePortAdapter implements AccountServicePort {

    private final AccountJpaRepository accountJpaRepository;
    @Override
    public List<NumberAccountStatisticProj> getAccountStatisticNumber() {
        return accountJpaRepository.getStatisticNumberOfAccountNoClosed(AccountStatusEnum.CLOSED);
    }

    @Override
    public List<SoldeAccountStatisticProj> getAccountStatisticSold() {
        return accountJpaRepository.getAccountStatisticSoldNoClosed(AccountStatusEnum.CLOSED);
    }

    @Override
    public BigDecimal getSoldeTotalOfAccountInMga() {
        return accountJpaRepository.getSoldTotalOfAccountNoClosed(AccountStatusEnum.CLOSED);
    }

    @Override
    public Long getNbrTotalOfAccount() {
        return accountJpaRepository.getNbrTotalOfAccountNoClosed(AccountStatusEnum.CLOSED);
    }
}
