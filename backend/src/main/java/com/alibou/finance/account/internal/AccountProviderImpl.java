package com.alibou.finance.account.internal;

import com.alibou.finance.account.AccountProvider;
import com.alibou.finance.account.application.port.usecase.AccountConsultationUseCase;
import com.alibou.finance.account.application.port.usecase.AccountStatisticUseCase;
import com.alibou.finance.shared.infrastructure.dto.NumberAccountStatistic;
import com.alibou.finance.shared.infrastructure.dto.SoldeAccountStatistic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountProviderImpl implements AccountProvider {
    private final AccountStatisticUseCase accountStatisticUseCase;
    private final AccountConsultationUseCase accountConsultationUseCase;

    @Override
    public List<NumberAccountStatistic> getAccountStatisticNumber() {
        return accountStatisticUseCase.getAccountStatisticNumber();
    }

    @Override
    public List<SoldeAccountStatistic> getAccountStatisticSold() {
        return accountStatisticUseCase.getAccountStatisticSold();
    }

    @Override
    public BigDecimal getSoldeTotalOfAccountInMga() {
        return accountStatisticUseCase.getSoldeTotalOfAccountInMga();
    }

    @Override
    public Long getNbrTotalOfAccount() {
        return accountStatisticUseCase.getNbrTotalOfAccount();
    }

}
