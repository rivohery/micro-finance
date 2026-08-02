package com.alibou.finance.auth.application.service;

import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.auth.domain.repository.UserRepository;
import com.alibou.finance.auth.domain.service.PasswordHasher;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.shared.error.domain.ObjectInvalidException;
import com.alibou.finance.shared.error.domain.OperationNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserApplicationService implements UserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    @Transactional
    public User create(User user) {
        validateUsernameUniqueness(user.getUsername().value());

        validateEmailUniqueness(user.getEmail().value());

        user.encodePassword(passwordHasher);
        return userRepository.save(user);
    }



    @Override
    @Transactional
    public User update(User user){
       User dbUser = getUserByUserId(user.getUserId());

       if(!Objects.equals(user.getUsername().value(), dbUser.getUsername().value())){
           validateUsernameUniqueness(user.getUsername().value());
           dbUser.updateUsername(user.getUsername());
       }
        if(!Objects.equals(user.getEmail().value(), dbUser.getEmail().value())){
            validateEmailUniqueness(user.getEmail().value());
            dbUser.updateEmail(user.getEmail());
        }
        if(user.getPassword() != null){
            dbUser.updatePassword(user.getPassword());
            dbUser.encodePassword(passwordHasher);
        }
        return userRepository.save(dbUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUserByUsername(String username, Pageable pageable) {
        return userRepository.searchUserByUsername(username, pageable);
    }

    @Override
    @Transactional
    public UUID delete(UserId userId) {
        checkIfUserExistById(userId);
        userRepository.deleteById(userId);
        return userId.value();
    }


    @Override
    @Transactional
    public void changePassword(String  username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Aucun utilisateur trouvé pour ce pseudo: %s", username))
        );
        user.changePassword(oldPassword, newPassword, passwordHasher);
        userRepository.changePassword(user.getUserId(), user.getPassword().value());
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUserId(UserId userId) {
        return getUserByUserId(userId);
    }

    @Override
    @Transactional
    public void disableUser(UserId userId){
        checkIfUserExistById(userId);
        userRepository.disableUser(userId);
    }

    @Override
    @Transactional
    public void changeUserStatus(UserId userId, boolean status) {
        checkIfUserExistById(userId);
        userRepository.changeStatus(userId, status);
    }

    private void checkIfUserExistById(UserId userId){
        boolean exists = userRepository.existsById(userId);
        if(!exists){
            throw new ObjectInvalidException(String.format("UserId invalide: %s", userId.value()));
        }
    }

    private User getUserByUserId(UserId userId){
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Utilisateur non trouvé: %s", userId.value()))
        );
    }

    private void validateUsernameUniqueness(String username){
        boolean isUsernameAlreadyExist = userRepository.existsByUsername(username);
        if(isUsernameAlreadyExist){
            throw new OperationNotPermittedException("Ce pseudo est déjà utilisé par un autre utilisateur");
        }
    }

    private void validateEmailUniqueness(String email){
        boolean isEmailAlreadyExist = userRepository.existsByEmail(email);
        if(isEmailAlreadyExist){
            throw new OperationNotPermittedException("Cette adresse email est deja utilisé");
        }
    }
}
