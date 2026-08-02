package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.account.application.port.dto.input.TransactionInput;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.account.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.account.domain.vo.transaction.TransactionCurrencyCode;
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
    public static TransactionInput toInput(TransactionRequest request, User user){
        return TransactionInput.builder()
                .concernedAccountNumber(new AccountNumber(request.accountNumber()))
                .description(new Description(request.description()))
                .user(user)
                .originalAmount(new OriginalAmount(request.amount()))
                .transactionCurrencyCode(new TransactionCurrencyCode(request.currencyCode()))
                .build();
    }
}
