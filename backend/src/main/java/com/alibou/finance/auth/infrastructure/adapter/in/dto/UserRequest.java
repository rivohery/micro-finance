package com.alibou.finance.auth.infrastructure.adapter.in.dto;

import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.auth.domain.agregate.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Le pseudo est obligatoire")
        @Size(min = 4, max = 20, message = "Le pseudo doit être compris entre 4 et 20 caractères")
        String username,
        @NotBlank(message = "Adresse email est obligatoire")
        @Email(message = "Adresse email est invalide")
        String email,
        RoleEnum role
) {
    public static User toDomain(UserRequest request){
        return User.create(request.username(), request.email(), request.role());
    }
}
