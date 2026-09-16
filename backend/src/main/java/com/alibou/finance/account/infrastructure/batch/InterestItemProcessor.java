package com.alibou.finance.account.infrastructure.batch;

import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.AccountMapper;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.transactional.AddMonthlyInterestUseCaseProxy;
import org.springframework.batch.item.ItemProcessor;

import java.util.UUID;

// Pas de @Component ici !
public class InterestItemProcessor implements ItemProcessor<AccountEntity, AccountEntity> {
    private final AddMonthlyInterestUseCaseProxy addMonthlyInterestService;
    private static final UUID SYSTEM_BATCH = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public InterestItemProcessor(AddMonthlyInterestUseCaseProxy addMonthlyInterestService) {
        this.addMonthlyInterestService = addMonthlyInterestService;
    }

    @Override
    public AccountEntity process(AccountEntity accountEntity) throws Exception {
        Account account = AccountMapper.entityToDomain(accountEntity);
        AccountEntity entityToSave = AccountMapper.updateEntityFromDomain(addMonthlyInterestService.execute(account), accountEntity);
        entityToSave.setCreatedBy(SYSTEM_BATCH);
        return entityToSave;
    }
}
