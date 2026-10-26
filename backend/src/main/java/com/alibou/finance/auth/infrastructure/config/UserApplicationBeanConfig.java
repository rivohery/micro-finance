package com.alibou.finance.auth.infrastructure.config;

import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.application.service.UserApplicationService;
import com.alibou.finance.auth.application.utils.UserApplicationHelper;
import com.alibou.finance.auth.application.utils.UserUniquenessChecker;
import com.alibou.finance.auth.domain.out.repository.UserRepository;
import com.alibou.finance.auth.domain.out.service.PasswordHasher;
import com.alibou.finance.auth.domain.out.event.UserEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserApplicationBeanConfig {

    @Bean
    public UserUniquenessChecker userUniquenessChecker(UserRepository userRepository){
        return new UserUniquenessChecker(userRepository);
    }

    @Bean
    public UserApplicationHelper userApplicationHelper(UserRepository userRepository, UserUniquenessChecker userUniquenessChecker){
        return new UserApplicationHelper(userRepository, userUniquenessChecker);
    }

    @Bean
    public UserUseCase createUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            UserUniquenessChecker userUniquenessChecker,
            UserEventPublisher userEventPublisher,
            UserApplicationHelper userApplicationHelper
    ){
       return new UserApplicationService(
               userRepository,
               passwordHasher,
               userUniquenessChecker,
               userEventPublisher,
               userApplicationHelper
       );
    }
}

