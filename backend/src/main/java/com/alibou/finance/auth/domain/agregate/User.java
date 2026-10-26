package com.alibou.finance.auth.domain.agregate;

import com.alibou.finance.auth.domain.out.service.PasswordHasher;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Email;
import com.alibou.finance.auth.domain.vo.Password;
import com.alibou.finance.auth.domain.vo.Role;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.shared.domain.exception.IllegalArgumentException;
import lombok.*;

import java.util.Objects;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    private UserId userId;
    private Username username;
    private Email email;
    private Password password;
    private boolean enable;
    private Role role;

    public boolean hasEmailChanged(Email newEmail){
        return !Objects.equals(this.email, newEmail);
    }

    public boolean hasUsernameChanged(Username username){
        return !Objects.equals(this.username, username);
    }

    public boolean isClient(){
        return this.role.value().equals(RoleEnum.CLIENT);
    }

    public void updateUsername(Username username){
        if(username != null){
            this.username = username;
        }
    }

    public void updateEmail(Email email){
        if(email != null){
            this.email = email;
        }
    }

    public void updatePassword(Password password){
        if(password != null){
            this.password = password;
        }
    }

    public void initDBValue(){
        this.password = Password.setDefaultPswd();
        this.userId = UserId.generate();
        this.enable = true;
    }

    public static User create(Username username, Email email, RoleEnum roleEnum){
        Role role = (roleEnum == null) ? Role.employe() : new Role(roleEnum);
        return User.builder()
                .username(username)
                .email(email)
                .role(role)
                .build();
    }

    public void encodePassword(PasswordHasher passwordHasher){
        this.password = Password.hash(this.password.value(), passwordHasher);
    }

    public void changePassword(Password oldPassword, Password newPassword, PasswordHasher passwordHasher) {
        if (!passwordHasher.matches(oldPassword.value(), this.password.value())) {
            throw new IllegalArgumentException("Mots de passe est incorrecte");
        }
        this.password = Password.hash(newPassword.value(), passwordHasher);
    }

}
