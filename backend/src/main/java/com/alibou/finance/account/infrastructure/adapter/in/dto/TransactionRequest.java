package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.account.application.port.command.DepositCommand;
import com.alibou.finance.account.application.port.command.WithdrawCommand;
import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.account.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.account.domain.vo.transaction.TransactionCurrencyCode;
import com.alibou.finance.shared.domain.vo.Description;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotBlank(message = "Le numéros du compte null")
        String accountNumber,
        @NotNull(message = "Montant de la transaction null")
        BigDecimal amount,
        @NotBlank(message= "Monnaie de la transaction null")
        String currencyCode,
        @NotNull(message = "Description de la transaction  null")
        @NotBlank(message = "Description de la transaction  null")
        String description

) {
    public static DepositCommand toDepositCommand(TransactionRequest request, UserInfo userInfo){
        return DepositCommand.builder()
                .accountNumber(new AccountNumber(request.accountNumber()))
                .description(new Description(request.description()))
                .originalAmount(new OriginalAmount(request.amount()))
                .transactionCurrencyCode(new TransactionCurrencyCode(request.currencyCode()))
                .username(userInfo.username())
                .build();
    }

    public static WithdrawCommand toWithdrawCommand(TransactionRequest request, UserInfo userInfo){
        return WithdrawCommand.builder()
                .accountNumber(new AccountNumber(request.accountNumber()))
                .description(new Description(request.description()))
                .originalAmount(new OriginalAmount(request.amount()))
                .transactionCurrencyCode(new TransactionCurrencyCode(request.currencyCode()))
                .username(userInfo.username())
                .build();
    }
}
