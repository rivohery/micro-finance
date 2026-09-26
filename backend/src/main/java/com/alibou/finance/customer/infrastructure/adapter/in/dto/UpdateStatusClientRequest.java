package com.alibou.finance.customer.infrastructure.adapter.in.dto;

import com.alibou.finance.customer.domain.agregate.CustomerStatusEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateStatusClientRequest(
        @NotNull
        UUID id,
        @NotNull
        CustomerStatusEnum status
) {
}
