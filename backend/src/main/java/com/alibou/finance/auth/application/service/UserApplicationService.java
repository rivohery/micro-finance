package com.alibou.finance.auth.application.service;

import com.alibou.finance.auth.application.command.ChangePasswordCommand;
import com.alibou.finance.auth.application.command.CreateUserCommand;
import com.alibou.finance.auth.application.command.UpdateUserCommand;
import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.application.utils.UserApplicationHelper;
import com.alibou.finance.auth.application.utils.UserUniquenessChecker;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.out.repository.UserRepository;
import com.alibou.finance.auth.domain.out.service.PasswordHasher;
import com.alibou.finance.auth.domain.out.event.UserEventPublisher;
import com.alibou.finance.shared.domain.event.UserEmailChangedEvent;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.application.result.PageResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserApplicationService implements UserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final UserUniquenessChecker userUniquenessChecker;
    private final UserEventPublisher userEventPublisher;
    private final UserApplicationHelper userApplicationHelper;

    @Override
    public User create(CreateUserCommand cmd) {
        userUniquenessChecker.ensureUsernameAvailable(cmd.username());
        userUniquenessChecker.ensureEmailAvailable(cmd.email());

        User user = User.create(cmd.username(), cmd.email(), cmd.roleEnum());
        user.initDBValue();
        user.encodePassword(passwordHasher);
        User saved =  userRepository.save(user);
        return saved;
    }

    @Override
    public User updateProfil(UpdateUserCommand cmd){
       User dbUser = userApplicationHelper.getUserByUserId(cmd.userId());

        userApplicationHelper.applyUsernameChange(dbUser, cmd);
        boolean emailChanged = userApplicationHelper.applyEmailChange(dbUser, cmd);
        boolean passwordChanged = userApplicationHelper.applyPasswordChange(dbUser, cmd);
        if(passwordChanged){
            dbUser.encodePassword(passwordHasher);
        }
        User saved =  userRepository.save(dbUser);
        if(emailChanged && saved.isClient()){
            UserEmailChangedEvent event = new UserEmailChangedEvent(
                 saved.getUserId(),
                 saved.getEmail()
            );
            userEventPublisher.publish(event);
        }
        return saved;
    }

    @Override
    public PageResult<User> searchUserByUsername(String username, int page, int size) {
        return userRepository.searchUserByUsername(username, page, size);
    }

    @Override
    public UserId delete(UserId userId) {
        userApplicationHelper.ensureUserExists(userId);
        userRepository.deleteById(userId);
        return userId;
    }


    @Override
    public void changePassword(ChangePasswordCommand cmd) {
        User user = userApplicationHelper.getUserByUsername(cmd.username());

        user.changePassword(cmd.oldPassword(), cmd.newPassword(), passwordHasher);
        userRepository.changePassword(user.getUserId(), user.getPassword());
    }

    @Override
    public User findByUserId(UserId userId) {
        return userApplicationHelper.getUserByUserId(userId);
    }

    @Override
    public void disableUser(UserId userId){
        userApplicationHelper.ensureUserExists(userId);
        userRepository.disableUser(userId);
    }

    @Override
    public void changeUserStatus(UserId userId, boolean status) {
        userApplicationHelper.ensureUserExists(userId);
        userRepository.changeStatus(userId, status);
    }
}
