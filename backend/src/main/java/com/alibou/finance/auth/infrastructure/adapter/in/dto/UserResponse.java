package com.alibou.finance.auth.infrastructure.adapter.in.dto;

import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private boolean enable;
    private RoleEnum role;
}
