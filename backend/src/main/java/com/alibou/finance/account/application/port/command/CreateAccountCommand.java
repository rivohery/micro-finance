package com.alibou.finance.account.application.port.command;

import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.user.CustomerId;

public record CreateAccountCommand (
        AccountTypeCode accountTypeCode,
        CurrencyCode currencyCode,
        CustomerId customerId

){
}
