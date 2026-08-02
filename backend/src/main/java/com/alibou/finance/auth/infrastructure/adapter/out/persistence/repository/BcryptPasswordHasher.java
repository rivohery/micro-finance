package com.alibou.finance.auth.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.auth.domain.service.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BcryptPasswordHasher implements PasswordHasher {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public String hash(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
