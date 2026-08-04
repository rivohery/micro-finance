package com.alibou.finance.account.infrastructure.adapter.in.dto;

import com.alibou.finance.account.application.port.dto.input.TransactionInput;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.log.domain.vo.transaction.OriginalAmount;
import com.alibou.finance.shared.vo.domain.Description;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransfertRequest(
        @NotNull(message="Numéros du compte source non null")
        @NotBlank(message="Numéros du compte source non null")
        String sourceAccountNumber,
        @NotNull
        @NotBlank(message = "Numéro du compte cible non renseigné")
        String targetAccountNumber,
        @NotNull
        @NotBlank(message="Description du transfert non renseigné")
        String description,
        @NotNull(message="Montant du transfert absent")
        @Positive(message="Montant du transfert invalide")
        BigDecimal transfertAmount
) {

    public static TransactionInput requestToInput(TransfertRequest request, User user){
        return TransactionInput
                .builder()
                .concernedAccountNumber(new AccountNumber(request.sourceAccountNumber()))
                .targetAccountNumber(new AccountNumber(request.targetAccountNumber()))
                .description(new Description(request.description()))
                .user(user)
                .originalAmount(new OriginalAmount(request.transfertAmount()))
                .build();
    }
}
