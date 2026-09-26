package com.alibou.finance.auth.domain.out.repository;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.Password;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.shared.application.result.PageResult;
import com.alibou.finance.shared.domain.vo.user.Email;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(Username username);
    User save(User user);
    PageResult<User> searchUserByUsername(String username, int page, int size);
    Optional<User>findById(UserId userId);

    void deleteById(UserId userId);

    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);
    void changePassword(UserId userId, Password newPassword);

    void disableUser(UserId userId);

    boolean existsById(UserId userId);
    void changeStatus(UserId userId, boolean status);

}
