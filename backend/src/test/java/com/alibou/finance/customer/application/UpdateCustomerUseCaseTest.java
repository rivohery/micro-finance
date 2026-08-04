package com.alibou.finance.customer.application;

import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.Address;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.customer.application.port.UpdateCustomerUseCase;
import com.alibou.finance.customer.application.service.UpdateCustomerServiceApplication;
import com.alibou.finance.customer.domain.agregate.Customer;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserUseCase userUseCase;
    @Mock
    private FileStoragePort fileStoragePort;

    @InjectMocks
    private UpdateCustomerServiceApplication updateCustomerService;
    byte[] contentFile = "image.jpg".getBytes();
    String fileName = "image.jpg";
    Customer customer;
    User user;

    @BeforeEach
    void setUp(){
        user = User.builder()
                .userId(UserId.generate())
                .email(new Email("john@gmail.com"))
                .username(new Username("john"))
                .build();
        customer = Customer.builder()
                .customerId(CustomerId.generate())
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
    @DisplayName("Teste la modification d'un client avec success")
    void shouldUpdateCustomerSuccessfully(){
        when(customerRepository.findById(any(CustomerId.class))).thenReturn(Optional.of(customer));
        when(fileStoragePort.uploadFile(any(), anyString(), anyString())).thenReturn("./uploads/clients/image.jpg");
        when(userUseCase.update(any(User.class))).thenAnswer(i-> i.getArgument(0));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i-> i.getArgument(0));

        Customer updated = updateCustomerService.execute(customer, contentFile, fileName);

        assertThat(updated.getCustomerId().value()).isEqualTo(customer.getCustomerId().value());
        assertThat(updated.getImageUrl().value()).isEqualTo("./uploads/clients/image.jpg");
        assertThat(updated.getUser().getUserId().value()).isEqualTo(user.getUserId().value());
        assertThat(updated.getCin().value()).isEqualTo("117071009630");
        assertThat(updated.getAddress().value()).isEqualTo("IPA 27");
        assertThat(updated.getDateOfBirth().value().compareTo(customer.getDateOfBirth().value())).isEqualTo(0);

        verify(customerRepository).findById(any(CustomerId.class));
        verify(fileStoragePort).uploadFile(any(), anyString(), anyString());
        verify(userUseCase).update(any(User.class));
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Devrait lever une exception lorsque la methode uploadFile est en échec")
    void shouldThrowExceptionWhenUploadFileFails(){
        when(customerRepository.findById(any(CustomerId.class))).thenReturn(Optional.of(customer));
        when(fileStoragePort.uploadFile(any(), anyString(), anyString())).thenThrow(new RuntimeException("Upload File fails"));

        assertThatThrownBy(() -> updateCustomerService.execute(customer, contentFile, fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Upload File fails");


        verify(customerRepository).findById(any(CustomerId.class));
        verify(fileStoragePort).uploadFile(any(), anyString(), anyString());
        verify(userUseCase, never()).update(any(User.class));
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
