package com.alibou.finance.account.infrastructure.batch;

import com.alibou.finance.account.application.port.usecase.CalculateMonthlyInterestUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.AccountMapper;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.transactional.CalculateMonthlyInterestUseCaseProxy;
import org.springframework.batch.item.ItemProcessor;

import java.util.UUID;

// Pas de @Component ici !
public class InterestItemProcessor implements ItemProcessor<AccountEntity, AccountEntity> {
    private final CalculateMonthlyInterestUseCaseProxy calculateMonthlyInterestService;
    private static final UUID SYSTEM_BATCH = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public InterestItemProcessor(CalculateMonthlyInterestUseCaseProxy calculateMonthlyInterestService) {
        this.calculateMonthlyInterestService = calculateMonthlyInterestService;
    }

    @Override
    public AccountEntity process(AccountEntity accountEntity) throws Exception {
        Account account = AccountMapper.entityToDomain(accountEntity);
        AccountEntity entityToSave = AccountMapper.updateEntityFromDomain(calculateMonthlyInterestService.execute(account), accountEntity);
        entityToSave.setCreatedBy(SYSTEM_BATCH);
        return entityToSave;
    }
}
