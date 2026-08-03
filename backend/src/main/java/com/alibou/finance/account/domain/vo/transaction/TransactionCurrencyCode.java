package com.alibou.finance.account.domain.vo.transaction;

import com.alibou.finance.shared.domain.Assert;

public record TransactionCurrencyCode(String value) {
    public TransactionCurrencyCode {
        Assert.field("TransactionCurrencyCode", value).notEmpty();
    }
}
