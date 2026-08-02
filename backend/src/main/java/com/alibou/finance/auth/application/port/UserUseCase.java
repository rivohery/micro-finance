package com.alibou.finance.auth.application.port;

import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.auth.domain.vo.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserUseCase {
    User create(User user);
    User update(User user);
    Page<User> searchUserByUsername(String username, Pageable pageable);
    UUID delete(UserId userId);
    void changePassword(String username, String oldPassword, String newPassword);
    User findByUserId(UserId userId);

    void disableUser(UserId userId);

    void changeUserStatus(UserId userId, boolean status);

}
