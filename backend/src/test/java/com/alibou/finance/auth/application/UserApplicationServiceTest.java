package com.alibou.finance.auth.application;

import com.alibou.finance.auth.application.service.UserApplicationService;
import com.alibou.finance.auth.domain.repository.UserRepository;
import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.auth.domain.service.PasswordHasher;
import com.alibou.finance.auth.infrastructure.adapter.in.dto.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserApplicationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordHasher passwordHasher;
    @InjectMocks
    private UserApplicationService userApplicationService;

    @Test
    void should_hash_password_and_save_user() {
        // 1. Given (Arrangement)
        var user = User.create("john_doe", "john@example.com", null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("hashed_password_123");

        // On capture l'utilisateur qui sera envoyé à userRepository.save()
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(i -> i.getArgument(0));

        // 2. When (Action)
        User savedUser = userApplicationService.create(user);


        //pour verifier que les mots de passe est bien encodé
        Assertions.assertEquals("hashed_password_123", savedUser.getPassword().value());

        Mockito.verify(passwordHasher, times(1)).hash(anyString());
        Mockito.verify(userRepository).save(any(User.class));
    }
}
