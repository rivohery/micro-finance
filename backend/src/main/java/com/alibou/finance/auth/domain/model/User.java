package com.alibou.finance.auth.domain.model;

import com.alibou.finance.auth.domain.service.PasswordHasher;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.shared.vo.domain.Email;
import com.alibou.finance.auth.domain.vo.Password;
import com.alibou.finance.auth.domain.vo.Role;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.shared.error.domain.IllegalArgumentException;
import lombok.*;

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
        this.username = username;
    }

    public void updateEmail(Email email){
        this.email = email;
    }

    public void updatePassword(Password password){
        this.password = password;
    }

    /* Utiliser pour la création de l'objet User*/
    private User(Username username, Email email, Role role, Password password){
        this.userId = UserId.generate();
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;//rawPassword
        this.enable = true;
    }

    /* Utiliser pour la modification de l'objet User */
    private User(UserId userId, Username username, Email email){
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public static User create(String usernameValue, String emailValue, RoleEnum roleValue){
        var username = new Username(usernameValue);
        var email = new Email(emailValue);
        var role = (roleValue == null) ? Role.employe() : new Role(roleValue);
        var password = Password.setDefaultPswd();//rawPassword
        return new User(username, email, role, password);
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
