package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.account.application.port.dto.command.DepositCommand;
import com.alibou.finance.account.application.port.dto.command.WithdrawCommand;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.log.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.log.domain.vo.transaction.TransactionCurrencyCode;
import com.alibou.finance.shared.vo.domain.Description;
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
    public static DepositCommand toDepositCommand(TransactionRequest request, User user){
        return DepositCommand.builder()
                .accountNumber(new AccountNumber(request.accountNumber()))
                .description(new Description(request.description()))
                .originalAmount(new OriginalAmount(request.amount()))
                .transactionCurrencyCode(new TransactionCurrencyCode(request.currencyCode()))
                .user(user)
                .build();
    }

    public static WithdrawCommand toWithdrawCommand(TransactionRequest request, User user){
        return WithdrawCommand.builder()
                .accountNumber(new AccountNumber(request.accountNumber()))
                .description(new Description(request.description()))
                .originalAmount(new OriginalAmount(request.amount()))
                .transactionCurrencyCode(new TransactionCurrencyCode(request.currencyCode()))
                .user(user)
                .build();
    }
}
