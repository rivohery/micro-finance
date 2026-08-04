package com.alibou.finance.auth.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.repository.UserRepository;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.auth.infrastructure.adapter.out.mapper.UserMapper;
import com.alibou.finance.auth.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDbAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(UserMapper::entityToDomain);
    }

    @Override
    public User save(User user) {
        var userEntity = UserMapper.domainToEntity(user);
        userEntity = userJpaRepository.save(userEntity);
        return UserMapper.entityToDomain(userEntity);
    }

    @Override
    public PageResult<User> searchUserByUsername(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> pages = userJpaRepository.searchAllEmployeeByUsernameStart(username, pageable);
        return PageMapper.toPageResult(pages, UserMapper::entityToDomain);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return userJpaRepository.findById(userId.value()).map(UserMapper::entityToDomain);
    }
    @Override
    public void deleteById(UserId userId) {
        userJpaRepository.deleteById(userId.value());
    }

    @Override
    public void changePassword(UserId userId, String password) {
        userJpaRepository.changePassword(userId.value(), password);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public void disableUser(UserId userId) {
        userJpaRepository.disableUser(userId.value());
    }

    @Override
    public void changeStatus(UserId userId, boolean status) {
        userJpaRepository.changeStatus(userId.value(), status);
    }

    @Override
    public boolean existsById(UserId userId) {
        return userJpaRepository.existsById(userId.value());
    }
}
