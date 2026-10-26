package com.alibou.finance.auth.infrastructure.transactional;

import com.alibou.finance.auth.application.command.ChangePasswordCommand;
import com.alibou.finance.auth.application.command.CreateUserCommand;
import com.alibou.finance.auth.application.command.UpdateUserCommand;
import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.application.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserUseCaseProxy {
    private final UserUseCase userUseCase;

    @Transactional
    public User create(CreateUserCommand cud){
       return userUseCase.create(cud);
    }

    @Transactional
    public User updateProfil(UpdateUserCommand updateUserCommand){
       return userUseCase.updateProfil(updateUserCommand);
    }
    @Transactional(readOnly = true)
    public PageResult<User> searchUserByUsername(String username, int page, int size){
        return userUseCase.searchUserByUsername(username, page, size);
    }
    @Transactional
    public UUID delete(UserId userId){
        return userUseCase.delete(userId).value();
    }
    @Transactional
    public void changePassword(ChangePasswordCommand cmd){
        userUseCase.changePassword(cmd);
    }
    @Transactional(readOnly = true)
    public User findByUserId(UserId userId){
        return userUseCase.findByUserId(userId);
    }

    @Transactional
    public void disableUser(UserId userId){
        userUseCase.disableUser(userId);
    }

    @Transactional
    public void changeUserStatus(UserId userId, boolean status){
        userUseCase.changeUserStatus(userId, status);
    }
}
