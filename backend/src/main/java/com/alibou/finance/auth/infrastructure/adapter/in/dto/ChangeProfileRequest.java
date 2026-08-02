package com.alibou.finance.auth.infrastructure.adapter.in.dto;

import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.auth.domain.vo.Password;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.shared.vo.domain.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeProfileRequest(
        @NotNull
        UUID id,
        String username,
        String email,
        String password
) {

    public static User toDomain(ChangeProfileRequest request){
        User user =  User.builder()
                .userId(UserId.from(request.id()))
                .username(new Username(request.username()))
                .email(new Email(request.email()))
                .build();
        if(request.password() != null && !request.password().isBlank()){
            user.updatePassword(new Password(request.password()));
        }
        return user;
    }
}
