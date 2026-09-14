package com.alibou.finance.auth.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeUserStatusRequest(
        @NotNull
        UUID userId,
        @NotNull
        boolean status
) {
}
