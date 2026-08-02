package com.alibou.finance.customer.infrastructure.adapter.in.dto;

import com.alibou.finance.customer.domain.model.CustomerStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateStatusClientRequest(
        @NotNull
        UUID id,
        @NotNull
        CustomerStatus status
) {
}
