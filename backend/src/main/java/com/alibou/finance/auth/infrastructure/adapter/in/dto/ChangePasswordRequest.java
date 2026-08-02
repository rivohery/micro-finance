package com.alibou.finance.auth.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message="Id de l'utilisateur est non nulle")
        String username,
        @NotBlank(message="L'ancien mots de passe est obligatoire")
        String oldPasswordPlain,
        @NotBlank(message="Le nouveau mots de passe est obligatoire")
        @Size(min= 4, message = "Le nouveau mots de passe est trop court")
        String newPasswordPlain
) {
}
