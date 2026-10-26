package com.alibou.finance.customer.application;

import com.alibou.finance.customer.domain.vo.Address;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.customer.application.command.UpdateCustomerCommand;
import com.alibou.finance.customer.application.service.UpdateCustomerServiceApplication;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.domain.vo.user.Email;
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
    private FileStoragePort fileStoragePort;

    @InjectMocks
    private UpdateCustomerServiceApplication updateCustomerService;
    byte[] contentFile = "image.jpg".getBytes();
    String fileName = "image.jpg";
    Customer dbCustomer;
    UserId userId;
    CustomerId customerId;

    UpdateCustomerCommand updateCustomerCommand;

    @BeforeEach
    void setUp(){
        userId = UserId.generate();
        customerId = CustomerId.generate();
        updateCustomerCommand = new UpdateCustomerCommand(
               customerId,
               userId,
               new FirstName("newFirstName"),
               new LastName("newLastName"),
               new DateOfBirth(LocalDate.of(2000,1,2)),
               new PhoneNumber( "0347366212"),
               new Cin("117071009630"),
               new Occupation( "occupation"),
                "Ipa 27 Bis",
                "newAddressCity",
                "101",
                "newAddressCountry"
        );
        dbCustomer = Customer.builder()
                .customerId(customerId)
                .userId(userId)
                .address(new Address("Ipa 27 Bis", "Antananarivo", "101", "Madagascar"))
                .cin(new Cin("117071009630"))
                .dateOfBirth(new DateOfBirth(LocalDate.of(2001,1,2)))
                .email(new Email("oldEmail@gmail.com"))
                .firstName(new FirstName("oldFirstName"))
                .lastName(new LastName("oldLastName"))
                .occupation(new Occupation("occupation"))
                .phoneNumber(new PhoneNumber("0347366212"))
                .imageUrl(new ImageUrl("oldImageUrl"))
                .build();

    }

    @Test
    @DisplayName("Teste la modification d'un client avec success")
    void shouldUpdateFirstName_LastName_Username_Email_address_Successfully(){
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(dbCustomer));
        when(fileStoragePort.uploadFile(any(), anyString(), anyString())).thenReturn("./uploads/clients/image.jpg");
        when(customerRepository.save(any(Customer.class))).thenAnswer(i-> i.getArgument(0));


        Customer updated = updateCustomerService.execute(updateCustomerCommand, contentFile, fileName);

        assertThat(updated.getCustomerId().value()).isEqualTo(dbCustomer.getCustomerId().value());
        assertThat(updated.getFirstName().value()).isEqualTo("newFirstName");
        assertThat(updated.getLastName().value()).isEqualTo("newLastName");
        assertThat(updated.getEmail().value()).isEqualTo("oldemail@gmail.com");
        assertThat(updated.getImageUrl().value()).isEqualTo("./uploads/clients/image.jpg");
        assertThat(updated.getUserId().value()).isEqualTo(userId.value());
        assertThat(updated.getCin().value()).isEqualTo("117071009630");
        assertThat(updated.getAddress().value()).isEqualTo("Ipa 27 Bis");
        assertThat(updated.getAddress().zipCode()).isEqualTo("101");
        assertThat(updated.getAddress().city()).isEqualTo("newAddressCity");
        assertThat(updated.getAddress().country()).isEqualTo("newAddressCountry");
        assertThat(updated.getDateOfBirth().value().equals(LocalDate.of(2000,1,2))).isTrue();

        verify(customerRepository).findById(any(CustomerId.class));
        verify(fileStoragePort).uploadFile(any(), anyString(), anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Devrait lever une exception lorsque la methode uploadFile est en échec")
    void shouldThrowExceptionWhenUploadFileFails(){
        when(customerRepository.findById(any(CustomerId.class))).thenReturn(Optional.of(dbCustomer));
        when(fileStoragePort.uploadFile(any(), anyString(), anyString())).thenThrow(new RuntimeException("Upload File fails"));

        assertThatThrownBy(() -> updateCustomerService.execute(updateCustomerCommand, contentFile, fileName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Upload File fails");


        verify(customerRepository).findById(any(CustomerId.class));
        verify(fileStoragePort).uploadFile(any(), anyString(), anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

}
