package com.alibou.finance.auth.application.port;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.shared.application.PageResult;

import java.util.UUID;

public interface UserUseCase {
    User create(User user);
    User update(User user);
    PageResult<User> searchUserByUsername(String username, int page, int size);
    UUID delete(UserId userId);
    void changePassword(String username, String oldPassword, String newPassword);
    User findByUserId(UserId userId);

    void disableUser(UserId userId);

    void changeUserStatus(UserId userId, boolean status);

}
