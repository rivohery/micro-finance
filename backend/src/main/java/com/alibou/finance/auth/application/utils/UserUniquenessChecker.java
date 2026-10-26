package com.alibou.finance.auth.application.utils;

import com.alibou.finance.auth.domain.out.repository.UserRepository;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.shared.domain.exception.OperationNotPermittedException;
import com.alibou.finance.shared.domain.vo.user.Email;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserUniquenessChecker {
    private final UserRepository userRepository;

    public void ensureUsernameAvailable(Username username) {
        if (userRepository.existsByUsername(username)) {
            throw new OperationNotPermittedException(
                    "Ce pseudo est déjà utilisé par un autre utilisateur"
            );
        }
    }

    public void ensureEmailAvailable(Email email) {
        if (userRepository.existsByEmail(email)) {
            throw new OperationNotPermittedException(
                    "Cette adresse email est déjà utilisée"
            );
        }
    }
}
