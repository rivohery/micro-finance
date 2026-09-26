package com.alibou.finance.account.application.port.command;

import com.alibou.finance.account.domain.vo.transaction.*;
import com.alibou.finance.shared.domain.vo.account.AccountId;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.shared.domain.vo.Description;
import com.alibou.finance.account.domain.vo.transaction.TransactionCurrencyCode;
import com.alibou.finance.shared.domain.vo.user.OperatorName;

public record CreateTransactionCommand(
        AccountId accountId,
        AccountNumber accountNumber,
        TransactionType transactionType,
        TransactionCurrencyCode transactionCurrencyCode,
        TargetCurrencyCode targetCurrencyCode,
        ExchangeRate exchangeRate,
        OriginalAmount originalAmount,
        SoldBeforeTransaction soldBeforeTransaction,
        Description description,
        OperatorName operatorName

) {
}

