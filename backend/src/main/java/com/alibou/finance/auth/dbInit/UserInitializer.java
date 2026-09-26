package com.alibou.finance.auth.dbInit;

import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.out.repository.UserRepository;
import com.alibou.finance.auth.domain.out.service.PasswordHasher;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.shared.domain.vo.user.Email;
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
        Username admin = new Username("admin");
        if(!userRepository.existsByUsername(admin)){
            try{
                var user = User.create(admin, new Email("admin@gmail.com"), RoleEnum.ADMIN);
                user.initDBValue();
                user.encodePassword(passwordHasher);
                userRepository.save(user);
            } catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }
}
