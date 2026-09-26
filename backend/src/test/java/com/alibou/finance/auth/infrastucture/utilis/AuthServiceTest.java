package com.alibou.finance.auth.infrastucture.utilis;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.auth.infrastructure.adapter.in.dto.LoginRequest;
import com.alibou.finance.auth.infrastructure.security.UserPrincipal;
import com.alibou.finance.auth.infrastructure.security.AuthService;
import com.alibou.finance.auth.infrastructure.security.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpiration", 3_600_000L); //1h
    }

    @Test
    void login_should_set_httponly_accessToken_cookie() {
        // --- Arrange ---
        LoginRequest request = new LoginRequest("john", "secret");

        var user = User.builder().userId(UserId.generate()).build();
        UserPrincipal principal = new UserPrincipal(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtService.generateToken(principal)).thenReturn("fake.jwt.token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // --- Act ---
        UUID userId = authService.login(request, response);

        // --- Assert ---
        assertThat(userId).isEqualTo(user.getUserId().value());

        Cookie cookie = response.getCookie("accessToken");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo("fake.jwt.token");
        assertThat(cookie.isHttpOnly()).isTrue();          // protégé contre XSS
        assertThat(cookie.getSecure()).isTrue();           // HTTPS uniquement
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge()).isEqualTo(3600);    // 3_600_000 ms / 1000
        assertThat(cookie.getAttributes().get("SameSite")).isEqualTo("Lax");

        String setCookie = response.getHeader("Set-Cookie");
        assertThat(setCookie).contains("HttpOnly");
    }

    @Test
    void logout_should_clear_cookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        authService.logout(response);

        Cookie cookie = response.getCookie("accessToken");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.getMaxAge()).isZero();
        assertThat(cookie.isHttpOnly()).isTrue();
    }
}
