package com.alibou.finance;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@TestConfiguration
@Primary
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "dateTimeProvider")
public class JpaAuditingTestConfig {
    public static final LocalDateTime FIXED_NOW = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
    public static final String FIXED_USER = "00000000-0000-0000-0000-000000000000";

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return () -> Optional.of(UUID.fromString(FIXED_USER));
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(FIXED_NOW);
    }
}
