package com.alibou.finance.auth.infrastructure.utils;

import com.alibou.finance.auth.infrastructure.adapter.in.dto.LoginRequest;
import com.alibou.finance.auth.infrastructure.model.UserPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UUID login(LoginRequest request, HttpServletResponse response) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        if (authentication.isAuthenticated()) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            prepareCookieForAccessToken(userPrincipal, response);
            return userPrincipal.getUser().getUserId().value();
        }
        return null;
    }

    public void logout(HttpServletResponse response){
        Cookie cookie = new Cookie("accessToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Lax");
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expirer le cookie immédiatement
        response.addCookie(cookie);
    }

    private void prepareCookieForAccessToken(UserPrincipal userPrincipal, HttpServletResponse response) {
        var accessToken = jwtService.generateToken(userPrincipal);

        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);//contre les attaques XSS
        cookie.setAttribute("SameSite", "Lax");
        cookie.setSecure(true); // À utiliser en production avec HTTPS
        cookie.setPath("/"); // Accessible depuis toutes les URLs
        cookie.setMaxAge((int) (jwtExpiration / 1000)); // En secondes
        response.addCookie(cookie);
    }

}
