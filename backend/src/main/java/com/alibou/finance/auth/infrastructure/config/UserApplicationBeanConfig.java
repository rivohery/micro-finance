package com.alibou.finance.auth.infrastructure.config;

import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.application.service.UserApplicationService;
import com.alibou.finance.auth.domain.repository.UserRepository;
import com.alibou.finance.auth.domain.service.PasswordHasher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserApplicationBeanConfig {
    @Bean
    public UserUseCase createUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher){
       return new UserApplicationService(userRepository, passwordHasher);
    }
}
