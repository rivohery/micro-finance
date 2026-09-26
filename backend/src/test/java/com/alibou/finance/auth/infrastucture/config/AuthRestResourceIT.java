package com.alibou.finance.auth.infrastucture.config;

import com.alibou.finance.BaseControllerIT;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.auth.infrastructure.security.UserPrincipal;
import com.alibou.finance.auth.infrastructure.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthRestResourceIT extends BaseControllerIT {

    @Autowired MockMvc mockMvc;
    @MockBean AuthenticationManager authenticationManager;
    @MockBean JwtService jwtService;

    @DisplayName("Pour prouver que dans la réponse HTTP réelle, on a bien :Set-Cookie: accessToken=...; HttpOnly; Secure; SameSite=Lax; Path=/" +
            "Set-Cookie: XSRF-TOKEN=... (généré par CookieCsrfTokenRepository + CsrfCookieFilter)")
    @Test
    void login_should_return_httponly_cookie_and_csrf_token() throws Exception {

        // --- Arrange ---
        var user = User.builder().userId(UserId.generate()).build();
        UserPrincipal principal = new UserPrincipal(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken(principal)).thenReturn("fake.jwt.token");

        // --- Act + Assert ---
        mockMvc.perform(post("/auths/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"john","password":"secret"}
                                """))
                // HTTP 200
                .andExpect(status().isOk())

                // Cookie accessToken : HttpOnly + Secure + Path=/
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().value("accessToken", "fake.jwt.token"))
                .andExpect(cookie().httpOnly("accessToken", true))
                .andExpect(cookie().secure("accessToken", true))
                .andExpect(cookie().path("accessToken", "/"))
                .andExpect(cookie().attribute("accessToken", "SameSite", "Lax"))

                // CSRF token généré par CookieCsrfTokenRepository
                .andExpect(cookie().exists("XSRF-TOKEN"))
                .andExpect(cookie().httpOnly("XSRF-TOKEN", false)); // doit être lisible par JS

    }

    @Test
    void logout_should_remove_cookie() throws Exception {

        mockMvc.perform(post("/auths/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().value("accessToken", ""))
                .andExpect(cookie().httpOnly("accessToken", true))
                .andExpect(cookie().secure("accessToken", true))
                .andExpect(cookie().path("accessToken", "/"))
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().exists("XSRF-TOKEN")) ;
    }
}
