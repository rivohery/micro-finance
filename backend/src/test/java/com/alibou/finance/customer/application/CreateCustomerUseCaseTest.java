package com.alibou.finance.customer.application;


import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.Address;
import com.alibou.finance.auth.domain.vo.Role;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.customer.application.service.CreateCustomerServiceApplication;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.agregate.CustomerStatus;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.shared.vo.domain.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private UserUseCase userUseCase;
    @Mock
    private FileStoragePort fileStoragePort;
    @InjectMocks
    private CreateCustomerServiceApplication createCustomerServiceApplication;

    byte[] contentFile = "image.jpg".getBytes();
    String fileName = "image.jpg";

    User user;
    Customer customer;

    @BeforeEach
    void setUp(){
        user = User.builder()
                .username(new Username("john"))
                .email(new Email("john@gmail.com"))
                .role(new Role(RoleEnum.CLIENT))
                .build();
        customer = Customer.builder()
                .user(user)
                .phoneNumber(new PhoneNumber("0347366212"))
                .firstName(new FirstName("John"))
                .dateOfBirth(new DateOfBirth(LocalDate.of(2000,1,2)))
                .lastName(new LastName("Doe"))
                .email(new Email("john@gmail.com"))
                .cin(new Cin("117071009630"))
                .address(new Address("IPA 27", "Tana", "102", "Mada"))
                .occupation(new Occupation("Dev"))
                .build();
    }

    @Test
    @DisplayName("Pour tester la création du client avec success")
    void shouldCreateCustomerWithSuccess(){
        when(customerRepository.existsByCin(anyString())).thenReturn(false);
        when(userUseCase.create(any(User.class))).thenAnswer(i->i.getArgument(0));
        when(fileStoragePort.uploadFile(contentFile, fileName, "clients")).thenReturn("./uploads/clients/image.jpg");
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer created = createCustomerServiceApplication.execute(customer, contentFile, fileName);

        assertThat(created).isNotNull();
        assertThat(created.getCustomerId().value()).isNotNull();
        assertThat(created.getImageUrl().value()).isEqualTo("./uploads/clients/image.jpg");
        assertThat(created.getStatus().value()).isEqualTo(CustomerStatus.PENDING);
        assertThat(created.getUser().getEmail().value()).isEqualTo("john@gmail.com");
        assertThat(created.getUser().getRole().value()).isEqualTo(RoleEnum.CLIENT);

        verify(userUseCase).create(any(User.class));
        verify(fileStoragePort).uploadFile(any(), anyString(), anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Pour tester si l'exception est levé si la création d'utilisateur est en échec")
    void shouldThrowExceptionAndNotSaveCustomerWhenUserCreationFails(){
        // 1. Given (Arrangement)
        when(customerRepository.existsByCin(anyString())).thenReturn(false);

        // On simule une erreur lors de l'appel au port de l'utilisateur
        when(userUseCase.create(any(User.class)))
                .thenThrow(new RuntimeException("Service Utilisateur indisponible"));

        // 2. When & Then
        assertThatThrownBy(() -> createCustomerServiceApplication.execute(customer, contentFile, fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service Utilisateur indisponible");

        verify(customerRepository).existsByCin(anyString());
        verify(userUseCase).create(any(User.class));
        verify(fileStoragePort, never()).uploadFile(contentFile, fileName, "clients");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionAndNotSaveCustomerWhenUploadFileFails(){
        // 1. Given (Arrangement)
        when(customerRepository.existsByCin(anyString())).thenReturn(false);
        when(userUseCase.create(any(User.class))).thenAnswer(i-> i.getArgument(0));
        when(fileStoragePort.uploadFile(contentFile, fileName, "clients"))
                .thenThrow(new RuntimeException("Upload file fails"));


        // 2. When & Then
        assertThatThrownBy(() -> createCustomerServiceApplication.execute(customer, contentFile, fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Upload file fails");

        verify(customerRepository).existsByCin(anyString());
        verify(userUseCase).create(any(User.class));
        verify(fileStoragePort).uploadFile(any(), anyString(), anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }
}


