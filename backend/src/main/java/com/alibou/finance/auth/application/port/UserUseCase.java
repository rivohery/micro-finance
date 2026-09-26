package com.alibou.finance.auth.application.port;

import com.alibou.finance.auth.application.command.ChangePasswordCommand;
import com.alibou.finance.auth.application.command.CreateUserCommand;
import com.alibou.finance.auth.application.command.UpdateUserCommand;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.application.result.PageResult;

public interface UserUseCase {
    User create(CreateUserCommand user);
    User updateProfil(UpdateUserCommand user);
    PageResult<User> searchUserByUsername(String username, int page, int size);
    UserId delete(UserId userId);
    void changePassword(ChangePasswordCommand cmd);
    User findByUserId(UserId userId);

    void disableUser(UserId userId);

    void changeUserStatus(UserId userId, boolean status);

}
