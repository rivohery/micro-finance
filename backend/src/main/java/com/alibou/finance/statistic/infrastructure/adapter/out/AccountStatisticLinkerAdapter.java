package com.alibou.finance.statistic.infrastructure.adapter.out;

import com.alibou.finance.account.AccountProvider;
import com.alibou.finance.shared.infrastructure.dto.NumberAccountStatistic;
import com.alibou.finance.shared.infrastructure.dto.SoldeAccountStatistic;
import com.alibou.finance.statistic.domain.out.AccountStatisticLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountStatisticLinkerAdapter implements AccountStatisticLinker {
    private final AccountProvider accountProvider;
    @Override
    public List<NumberAccountStatistic> getAccountStatisticNumber() {
        return accountProvider.getAccountStatisticNumber();
    }
    @Override
    public List<SoldeAccountStatistic> getAccountStatisticSold() {
        return accountProvider.getAccountStatisticSold();
    }
    @Override
    public BigDecimal getSoldeTotalOfAccountInMga() {
        return accountProvider.getSoldeTotalOfAccountInMga();
    }
    @Override
    public Long getNbrTotalOfAccount() {
        return accountProvider.getNbrTotalOfAccount();
    }
}
