package com.alibou.finance.auth.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Pseudo obligatoire")
        String username,
        @NotBlank(message = "Mots de passe obligatoire")
        String password
) {
}
