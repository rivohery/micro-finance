package com.alibou.finance.auth.infrastructure.adapter.in.controller;

import com.alibou.finance.auth.infrastructure.adapter.in.dto.LoginRequest;
import com.alibou.finance.auth.infrastructure.utils.AuthService;
import com.alibou.finance.shared.dto.GlobalResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auths")
@Tag(name="auths-endpoints", description = "Endpoint pour l'authentification")
@RequiredArgsConstructor
public class AuthRestResource {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GlobalResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ){
        UUID userId = authService.login(request, response);
        if(userId != null){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message("Authentification réussie")
                            .data(Map.of("userId", userId))
                            .build()
            );
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<GlobalResponse> logout(HttpServletResponse response){
        authService.logout(response);
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .message("Déconnexion réussie")
                        .build()
        );
    }

}
