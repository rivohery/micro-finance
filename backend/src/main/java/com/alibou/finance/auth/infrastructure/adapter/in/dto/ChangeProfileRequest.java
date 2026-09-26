package com.alibou.finance.auth.infrastructure.adapter.in.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ChangeProfileRequest(
        @NotNull
        UUID id,
        @NotBlank
        String username,
        @NotBlank
        String email,
        String password
) {
}

