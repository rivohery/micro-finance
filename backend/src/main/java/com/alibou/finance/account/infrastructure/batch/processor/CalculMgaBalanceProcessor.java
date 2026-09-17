package com.alibou.finance.account.infrastructure.batch.processor;

import com.alibou.finance.account.application.port.usecase.CalculMgaBalanceUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.AccountMapper;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.springframework.batch.item.ItemProcessor;

public class CalculMgaBalanceProcessor implements ItemProcessor<AccountEntity, AccountEntity> {
    private final CalculMgaBalanceUseCase calculMgaBalanceUseCase;
    public CalculMgaBalanceProcessor(CalculMgaBalanceUseCase calculMgaBalanceUseCase){
        this.calculMgaBalanceUseCase = calculMgaBalanceUseCase;
    }

    @Override
    public AccountEntity process(AccountEntity item) throws Exception {
        Account account = AccountMapper.entityToDomain(item);
        account = calculMgaBalanceUseCase.execute(account);
        return AccountMapper.updateEntityFromDomain(account, item);
    }
}
