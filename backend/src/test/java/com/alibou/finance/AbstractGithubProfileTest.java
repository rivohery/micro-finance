package com.alibou.finance;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("testcontainers")
@Import(TestcontainersConfiguration.class)
public abstract class AbstractGithubProfileTest {
}
