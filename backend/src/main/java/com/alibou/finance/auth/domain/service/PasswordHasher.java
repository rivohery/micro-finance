package com.alibou.finance.auth.domain.service;

public interface PasswordHasher {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
