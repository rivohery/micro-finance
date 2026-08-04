package com.alibou.finance.auth.infrastructure.transactional;

import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.shared.application.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserUseCaseProxy {
    private final UserUseCase userUseCase;

    @Transactional
    public User create(User user){
       return userUseCase.create(user);
    }

    @Transactional
    public User update(User user){
       return userUseCase.update(user);
    }
    @Transactional(readOnly = true)
    public PageResult<User> searchUserByUsername(String username, int page, int size){
        return userUseCase.searchUserByUsername(username, page, size);
    }
    @Transactional
    public UUID delete(UserId userId){
        return userUseCase.delete(userId);
    }
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword){
        userUseCase.changePassword(username, oldPassword, newPassword);
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
