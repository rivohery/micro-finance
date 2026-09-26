package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.account.application.port.command.CreateAccountCommand;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeCode;
import com.alibou.finance.shared.domain.vo.currency.CurrencyCode;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAccountRequest(
        @NotBlank(message="Type de compte obligatoire")
        String accountTypeCode,//10=>COURANTE|20=>ÉPARGNE|30=>BUSINESS
        String currencyCode,
        @NotNull(message="Client ID est obligatoire")
        UUID customerId
) {

    public static CreateAccountCommand toCreateAccountCommand(CreateAccountRequest request){
        var accountCurrencyCode = (request.currencyCode != null && !request.currencyCode.isBlank()) ? request.currencyCode : "MGA";
        return new CreateAccountCommand(
            new AccountTypeCode(request.accountTypeCode()),
            new CurrencyCode(accountCurrencyCode),
            new CustomerId(request.customerId())
        );
    }
}
