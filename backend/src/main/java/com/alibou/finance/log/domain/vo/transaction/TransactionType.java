package com.alibou.finance.log.domain.vo.transaction;

import com.alibou.finance.log.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.shared.domain.Assert;
import com.alibou.finance.shared.domain.IllegalArgumentException;

import java.util.Set;

public record TransactionType(TransactionTypeEnum value) {
    private static final Set<TransactionTypeEnum>transactions = Set.of(
            TransactionTypeEnum.DEPOSIT,
            TransactionTypeEnum.TRANSFERT,
            TransactionTypeEnum.WITHDRAWAL
    );
    public TransactionType{
        Assert.notNull("Type de transaction", value);
        if(!transactions.contains(value)){
            throw new IllegalArgumentException("Type de transaction inconnue par le system");
        }
    }
}
