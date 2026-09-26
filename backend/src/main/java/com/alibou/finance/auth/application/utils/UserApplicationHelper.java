package com.alibou.finance.auth.application.utils;

import com.alibou.finance.auth.application.command.UpdateUserCommand;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.out.repository.UserRepository;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.shared.domain.exception.ObjectInvalidException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserApplicationHelper {
    private final UserRepository userRepository;
    private final UserUniquenessChecker userUniquenessChecker;

    public boolean applyUsernameChange(User user, UpdateUserCommand cmd) {
        if (!user.hasUsernameChanged(cmd.username())) {
            return false;
        }
        userUniquenessChecker.ensureUsernameAvailable(cmd.username());
        user.updateUsername(cmd.username());
        return true;
    }

    public boolean applyEmailChange(User user, UpdateUserCommand cmd) {
        if (!user.hasEmailChanged(cmd.email())) {
            return false;
        }
        userUniquenessChecker.ensureEmailAvailable(cmd.email());
        user.updateEmail(cmd.email());
        return true;
    }

    public boolean applyPasswordChange(User user, UpdateUserCommand cmd) {
        if (cmd.password() == null) {
            return false;
        }
        user.updatePassword(cmd.password());
        return true;
    }

    public void ensureUserExists(UserId userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectInvalidException(
                    String.format("UserId invalide: %s", userId.value()));
        }
    }

    public User getUserByUserId(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Utilisateur non trouvé: %s", userId.value())));
    }

    public User getUserByUsername(Username username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Aucun utilisateur trouvé pour ce pseudo: %s", username.value()))
        );
    }
}
