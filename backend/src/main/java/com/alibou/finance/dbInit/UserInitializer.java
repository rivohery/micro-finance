package com.alibou.finance.dbInit;

import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.repository.UserRepository;
import com.alibou.finance.auth.domain.service.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Profile({"dev"})
@Order(value = 1)
@Component
@RequiredArgsConstructor
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    public void run(String... args) throws Exception {
        if(!userRepository.existsByUsername("alibou")){
            try{
                var user = User.create("alibou", "admin@gmail.com", RoleEnum.ADMIN);
                user.encodePassword(passwordHasher);
                userRepository.save(user);
            } catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }
}
