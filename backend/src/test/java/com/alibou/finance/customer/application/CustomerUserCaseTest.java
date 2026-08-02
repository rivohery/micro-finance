package com.alibou.finance.customer.application;

import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.model.RoleEnum;
import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.auth.domain.vo.Address;
import com.alibou.finance.auth.domain.vo.Role;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.customer.application.service.CustomerApplicationService;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.customer.domain.model.Customer;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.shared.vo.domain.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerUserCaseTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserUseCase userUseCase;
    @Mock
    private FileStoragePort fileStoragePort;
    @InjectMocks
    private CustomerApplicationService customerApplicationService;
    private byte[] fileContent = "photo-content".getBytes();
    private String fileName = "photo.jpg";
    Customer customer;

    @BeforeEach
    void setUp(){
        customer = Customer.builder()
                .customerId(CustomerId.generate())
                .cin(new Cin("117071009630"))
                .user(User.builder().build())
                .build();
    }

    @Test
    void shouldCreateCustomerSuccessfully(){
        //Given
        User user = User.builder()
                .userId(UserId.generate())
                .username(new Username("johndoe"))
                .role(new Role(RoleEnum.CLIENT))
                .build();
        when(customerRepository.existsByCin(eq("117071009630"))).thenReturn(false);
        when(userUseCase.create(any())).thenReturn(user);
        when(fileStoragePort.uploadFile(fileContent, fileName, "clients")).thenReturn("./uploads/clients/file.jpg");
        when(customerRepository.save(any())).thenReturn(customer);

        var result = customerApplicationService.create(customer, fileContent, fileName);

        assertThat(result).isNotNull();
        assertThat(result.getUser().getUserId().value()).isEqualTo(customer.getUser().getUserId().value());
        assertThat(result.getUser().getUsername().value()).isEqualTo(user.getUsername().value());
        assertThat(result.getImageUrl().value()).isEqualTo("./uploads/clients/file.jpg");
        verify(userUseCase).create(any(User.class));
        verify(fileStoragePort).uploadFile(fileContent, fileName, "clients");
        verify(customerRepository).save(any(Customer.class));

    }

    @Test
    void shouldUpdateCustomerSuccessfully(){
        //Given
        User user = User.builder()
                .userId(UserId.generate())
                .username(new Username("johndoe"))
                .role(new Role(RoleEnum.CLIENT))
                .email(new Email("alibou@gmail.com"))
                .build();
        CustomerId customerId = new CustomerId(UUID.randomUUID());
        Customer customer = Customer.builder()
                .cin(new Cin("117071009630"))
                .user(user)
                .customerId(customerId)
                .email(new Email("alibou@gmail.com"))
                .dateOfBirth(new DateOfBirth(LocalDate.of(2000,1,4)))
                .lastName(new LastName("Joe"))
                .firstName(new FirstName("Abraham"))
                .address(new Address("Lot Ipa 27 Ter", "Tana", "102", "Mada"))
                .occupation(new Occupation("Développeur"))
                .phoneNumber(new PhoneNumber("0347366212"))
                .build();
        Customer dbCustomer = Customer.builder().customerId(customerId).build();
        when(customerRepository.findById(any(CustomerId.class))).thenReturn(Optional.of(dbCustomer));
        when(fileStoragePort.uploadFile(fileContent, fileName, "clients")).thenReturn("./uploads/clients/file.jpg");
        when(userUseCase.update(any(User.class))).thenReturn(user);
        when(customerRepository.save(any(Customer.class))).thenReturn(dbCustomer);

        Customer updated = customerApplicationService.update(customer, fileContent, fileName);

        assertThat(updated.getCustomerId().value()).isEqualTo(customerId.value());
        assertThat(updated.getImageUrl().value()).isEqualTo("./uploads/clients/file.jpg");
        assertThat(updated.getUser().getUsername().value()).isEqualTo("johndoe");
        assertThat(updated.getUser().getEmail().value()).isEqualTo("alibou@gmail.com");
        assertThat(updated.getUser().getUserId().value()).isEqualTo(user.getUserId().value());
        assertThat(updated.getAddress().city()).isEqualTo("Tana");
        assertThat(updated.getAddress().value()).isEqualTo("Lot Ipa 27 Ter");
        assertThat(updated.getPhoneNumber().value()).isEqualTo("0347366212");
        assertThat(updated.getDateOfBirth().value().compareTo(LocalDate.of(2000,1,4))).isEqualTo(0);
        assertThat(updated.getEmail().value()).isEqualTo("alibou@gmail.com");
        assertThat(updated.getOccupation().value()).isEqualTo("Développeur");
        assertThat(updated.getFirstName().value()).isEqualTo("Abraham");
        verify(customerRepository, times(1)).findById(any(CustomerId.class));
        verify(userUseCase).update(any(User.class));
        verify(customerRepository).save(any(Customer.class));

    }

    @Test
    void shouldThrowExceptionAndNotSaveCustomerWhenUserCreationFails(){
        // 1. Given (Arrangement)
        String cin = "117071009630";
        when(customerRepository.existsByCin(cin)).thenReturn(false);

        // On simule une erreur lors de l'appel au port de l'utilisateur
        when(userUseCase.create(any(User.class)))
                .thenThrow(new RuntimeException("Service Utilisateur indisponible"));

        // 2. When & Then
        assertThatThrownBy(() -> customerApplicationService.create(customer, fileContent, fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service Utilisateur indisponible");

        verify(fileStoragePort, never()).uploadFile(fileContent, fileName, "clients");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionAndNotSaveCustomerWhenUploadFileFails(){
        // 1. Given (Arrangement)
        String cin = "117071009630";

        when(customerRepository.existsByCin(cin)).thenReturn(false);
        when(userUseCase.create(any())).thenReturn(User.builder().build());
        when(fileStoragePort.uploadFile(fileContent, fileName, "clients"))
                .thenThrow(new RuntimeException("Upload file fails"));


        // 2. When & Then
        assertThatThrownBy(() -> customerApplicationService.create(customer, fileContent, fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Upload file fails");

        verify(userUseCase).create(any());
        verify(customerRepository, never()).save(any(Customer.class));
    }

}
