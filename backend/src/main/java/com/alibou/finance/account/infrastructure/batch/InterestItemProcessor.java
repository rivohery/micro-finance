package com.alibou.finance.account.infrastructure.batch;

import com.alibou.finance.account.application.port.usecase.CalculateMonthlyInterestUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.AccountMapper;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.springframework.batch.item.ItemProcessor;

// Pas de @Component ici !
public class InterestItemProcessor implements ItemProcessor<AccountEntity, AccountEntity> {
    private final CalculateMonthlyInterestUseCase calculateMonthlyInterestService;

    public InterestItemProcessor(CalculateMonthlyInterestUseCase calculateMonthlyInterestService) {
        this.calculateMonthlyInterestService = calculateMonthlyInterestService;
    }

    @Override
    public AccountEntity process(AccountEntity accountEntity) throws Exception {
        Account account = AccountMapper.entityToDomain(accountEntity);
        return AccountMapper.domainToEntity(calculateMonthlyInterestService.execute(account));
    }
}
