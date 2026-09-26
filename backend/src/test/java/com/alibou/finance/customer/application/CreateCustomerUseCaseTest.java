package com.alibou.finance.customer.application;


import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.auth.application.command.CreateUserCommand;
import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.Role;
import com.alibou.finance.customer.domain.out.linker.UserLinkerPort;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.customer.application.command.CreateCustomerCommand;
import com.alibou.finance.customer.application.service.CreateCustomerServiceApplication;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.agregate.CustomerStatusEnum;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.shared.domain.vo.user.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateCustomerUseCaseTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserLinkerPort userLinkerPort;
    @Mock
    private FileStoragePort fileStoragePort;

    @InjectMocks
    private CreateCustomerServiceApplication createCustomerServiceApplication;

    byte[] contentFile = "image.jpg".getBytes();
    String fileName = "image.jpg";

    Customer customer;

    CreateCustomerCommand createCustomerCommand;

    @BeforeEach
    void setUp(){
        createCustomerCommand = new CreateCustomerCommand(
                new FirstName("John"),
                new LastName("Doe"),
                new Username("JohnDoe"),
                new DateOfBirth(LocalDate.of(2000,02,02)),
                new PhoneNumber("0347366212"),
                new Email("john@Gmail.com"),
                new Cin("117071009630"),
                new Occupation("développeur"),
                "IPA 27 Bis",
                "Tana",
                "101",
                "Mada"
        );
    }

    @Test
    @DisplayName("Pour tester la création du client avec success")
    void shouldCreateCustomerWithSuccess(){
        when(customerRepository.existsByCin(any(Cin.class))).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(any(PhoneNumber.class))).thenReturn(false);

        when(userLinkerPort.createUserFromCustomerInfo(createCustomerCommand.username(), createCustomerCommand.email())).thenAnswer(i-> {
            Username  username = i.getArgument(0);
            Email email = i.getArgument(1);
            return new UserInfo(
                    UserId.generate(),
                    username,
                    email
            );
        });

        when(fileStoragePort.uploadFile(contentFile, fileName, "clients")).thenReturn("./uploads/clients/image.jpg");
        ArgumentCaptor<Customer>customerCaptor = ArgumentCaptor.forClass(Customer.class);
        when(customerRepository.save(customerCaptor.capture())).thenAnswer(i -> i.getArgument(0));

        Customer created = createCustomerServiceApplication.execute(createCustomerCommand, contentFile, fileName);

        assertThat(created).isNotNull();
        assertThat(created.getCustomerId().value()).isNotNull();
        assertThat(created.getImageUrl().value()).isEqualTo("./uploads/clients/image.jpg");
        assertThat(created.getCustomerStatus().value()).isEqualTo(CustomerStatusEnum.PENDING_KYC);
        assertThat(created.getUserId()).isNotNull();

        verify(userLinkerPort).createUserFromCustomerInfo(any(), any());
        verify(fileStoragePort).uploadFile(any(), anyString(), anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Pour tester si l'exception est levé si la création d'utilisateur est en échec")
    void shouldThrowExceptionAndNotSaveCustomerWhenUserCreationFails(){
        // 1. Given (Arrangement)
        when(customerRepository.existsByCin(any(Cin.class))).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(any(PhoneNumber.class))).thenReturn(false);
        // On simule une erreur lors de l'appel au port de l'utilisateur
        when(userLinkerPort.createUserFromCustomerInfo(createCustomerCommand.username(), createCustomerCommand.email()))
                .thenThrow(new RuntimeException("Service Utilisateur indisponible"));


        // 2. When & Then
        assertThatThrownBy(() -> createCustomerServiceApplication.execute(createCustomerCommand, contentFile, fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service Utilisateur indisponible");

        verify(customerRepository).existsByCin(any(Cin.class));
        verify(userLinkerPort).createUserFromCustomerInfo(any(), any());
        verify(fileStoragePort, never()).uploadFile(contentFile, fileName, "clients");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionAndNotSaveCustomerWhenUploadFileFails(){
        // 1. Given (Arrangement)
        when(customerRepository.existsByCin(any(Cin.class))).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(any(PhoneNumber.class))).thenReturn(false);
        when(userLinkerPort.createUserFromCustomerInfo(createCustomerCommand.username(), createCustomerCommand.email())).thenAnswer(i-> {
            Username  username = i.getArgument(0);
            Email email = i.getArgument(1);
            return new UserInfo(
                    UserId.generate(),
                    username,
                    email
            );
        });

        when(fileStoragePort.uploadFile(contentFile, fileName, "clients"))
                .thenThrow(new RuntimeException("Upload file fails"));


        // 2. When & Then
        assertThatThrownBy(() -> createCustomerServiceApplication.execute(createCustomerCommand, contentFile, fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Upload file fails");

        verify(customerRepository).existsByCin(any(Cin.class));
        verify(userLinkerPort).createUserFromCustomerInfo(any(), any());
        verify(fileStoragePort).uploadFile(any(), anyString(), anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

}


