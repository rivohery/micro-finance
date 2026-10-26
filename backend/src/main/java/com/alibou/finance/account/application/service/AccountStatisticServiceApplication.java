package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.usecase.AccountStatisticUseCase;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.AccountStatisticMapper;
import com.alibou.finance.shared.infrastructure.dto.NumberAccountStatistic;
import com.alibou.finance.shared.infrastructure.dto.SoldeAccountStatistic;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class AccountStatisticServiceApplication implements AccountStatisticUseCase {
    private final AccountRepository accountRepository;
    @Override
    public List<NumberAccountStatistic> getAccountStatisticNumber() {
        return accountRepository.getStatisticNumberOfAccountNoClosed()
                .stream()
                .map(AccountStatisticMapper::toNumberAccountStatistic)
                .toList();
    }

    @Override
    public List<SoldeAccountStatistic> getAccountStatisticSold() {
        return accountRepository.getAccountStatisticSoldNoClosed()
                .stream()
                .map(AccountStatisticMapper::toSoldAccountStatistic)
                .toList();
    }

    @Override
    public BigDecimal getSoldeTotalOfAccountInMga() {
        return accountRepository.getSoldTotalOfAccountNoClosed();
    }

    @Override
    public Long getNbrTotalOfAccount() {
        return accountRepository.getNbrTotalOfAccountNoClosed();
    }
}
