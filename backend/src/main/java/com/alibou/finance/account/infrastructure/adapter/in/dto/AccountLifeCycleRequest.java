package com.alibou.finance.account.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AccountLifeCycleRequest(
        @NotNull(message = "ID du compte non nulle")
        UUID accountId,
        @NotBlank(message = "ce champs est obligatoire")
        @Size(min = 10, max = 255, message="La raison doit être compris entre 10 à 255 caractères")
        String reason
) { }
