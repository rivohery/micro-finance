package com.alibou.finance.auth.domain.repository;

import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.auth.domain.vo.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
    Page<User> searchUserByUsername(String username, Pageable pageable);
    Optional<User>findById(UserId userId);

    void deleteById(UserId userId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void changePassword(UserId userId, String password);

    void disableUser(UserId userId);

    boolean existsById(UserId userId);
    void changeStatus(UserId userId, boolean status);

}
