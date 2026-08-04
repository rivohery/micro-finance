package com.alibou.finance.auth.domain.agregate;

import com.alibou.finance.auth.domain.service.PasswordHasher;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.shared.vo.domain.Email;
import com.alibou.finance.auth.domain.vo.Password;
import com.alibou.finance.auth.domain.vo.Role;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.shared.domain.IllegalArgumentException;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Pour le Builder uniquement
public class User {
    private UserId userId;
    private Username username;
    private Email email;
    private Password password;
    private boolean enable;
    private Role role;

    public void updateUsername(Username username){
        if(Objects.equals(this.username.value(), username.value())){
            return;
        }
        this.username = username;
    }

    public void updateEmail(Email email){
        if(Objects.equals(this.email.value(), email.value())){
            return;
        }
        this.email = email;
    }

    public void updatePassword(Password password){
        if(Objects.isNull(password)){
            throw new IllegalArgumentException("la value objet Password est nulle");
        }
        this.password = password;
    }

    /* Utiliser pour la modification de l'objet User */
    private User(UserId userId, Username username, Email email){
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public void initDBValue(){
        this.password = Password.setDefaultPswd();
        this.userId = UserId.generate();
        this.enable = true;
    }

    public static User create(String username, String email, RoleEnum roleEnum){
        Role role = (roleEnum == null) ? Role.employe() : new Role(roleEnum);
        return User.builder()
                .username(new Username(username))
                .email(new Email(email))
                .role(role)
                .build();
    }

    public static User update(UUID idValue, String usernameValue, String emailValue){
        var userId = UserId.from(idValue);
        var username = new Username(usernameValue);
        var email = new Email(emailValue);
        return new User(userId, username, email);
    }

    public void encodePassword(PasswordHasher passwordHasher){
        this.password = Password.hash(this.password.value(), passwordHasher);
    }

    public void activate(){
        this.enable = true;
    }

    public void changePassword(String oldPasswordPlain, String newPasswordPlain, PasswordHasher hasher) {
        if (!hasher.matches(oldPasswordPlain, this.password.value())) {
            throw new IllegalArgumentException("Mots de passe est incorrecte");
        }
        Password newPassword = new Password(newPasswordPlain);
        this.password = Password.hash(newPassword.value(), hasher);
    }

}
