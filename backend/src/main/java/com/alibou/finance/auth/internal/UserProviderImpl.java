package com.alibou.finance.auth.internal;

import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.auth.UserProvider;
import com.alibou.finance.auth.application.command.CreateUserCommand;
import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.infrastructure.security.UserPrincipal;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.shared.domain.vo.user.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProviderImpl implements UserProvider {

    private final UserUseCase userUseCase;

    @Override
    public UserInfo createUserFromCustomerInfo(Username username, Email email) {
        CreateUserCommand cmd = new CreateUserCommand(
                username,
                email,
                RoleEnum.CLIENT
        );
        User user = userUseCase.create(cmd);
        return new UserInfo(
                user.getUserId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    @Override
    public UserInfo getUserByUserId(UserId userId) {
        User user = userUseCase.findByUserId(userId);
        return new UserInfo(
                user.getUserId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    @Override
    public void disableUser(UserId userId) {
        userUseCase.disableUser(userId);
    }

    @Override
    public UserInfo getUserConnected(Authentication authentication) {
        var object = authentication.getPrincipal();
        if(object instanceof UserPrincipal){
            User user = ((UserPrincipal) object).getUser();
            return new UserInfo(
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail()
            );
        }
        else {
            throw new AccessDeniedException("Aucun utilisateur connecté");
        }
    }
}
