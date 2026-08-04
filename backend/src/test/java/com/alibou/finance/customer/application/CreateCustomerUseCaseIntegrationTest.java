package com.alibou.finance.customer.application;

import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.alibou.finance.auth.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.alibou.finance.customer.application.port.CreateCustomerUseCase;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.repository.CustomerJpaRepository;
import com.alibou.finance.customer.infrastructure.transactional.CreateCustomerUseCaseProxy;
import com.alibou.finance.shared.vo.domain.Email;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class CreateCustomerUseCaseIntegrationTest {

    @Autowired
    private CustomerJpaRepository customerJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private CreateCustomerUseCase createCustomerUseCase;

    @Autowired
    private CreateCustomerUseCaseProxy createCustomerUseCaseProxy;

    @MockBean
    private BCryptPasswordEncoder mockBcrypt;

    @MockBean
    private FileStoragePort fileStoragePort;
    byte[] fileContent = "fileContent".getBytes();
    String fileName = "maPhoto";

    User user;
    Customer customer;

    @BeforeEach
    void setUp(){
        customerJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        user = User.create("johndoe", "johnDoe@gmail.com", RoleEnum.CLIENT);
        customer = Customer.builder()
                .customerId(CustomerId.generate())
                .firstName(new FirstName("John"))
                .lastName(new LastName("Doe"))
                .user(user)
                .cin(new Cin("117071009630"))
                .dateOfBirth(new DateOfBirth( LocalDate.of(2000, 2, 3)))
                .email(new Email("johnDoe@gmail.com"))
                .build();
    }

    @Test
    @DisplayName("Tester la transaction sur CreateCustomerUseCaseProxy dans la création d'un client si une exception est levée")
    void shouldTestTransactionalSuccessWhenUploadFileFails(){
        when(mockBcrypt.encode(anyString())).thenReturn("1234");
        when(fileStoragePort.uploadFile(fileContent, fileName, "clients"))
                .thenThrow(new RuntimeException("Upload file fails"));

        assertThatThrownBy(
                () -> createCustomerUseCaseProxy.execute(customer,fileContent, fileName)
        ).isInstanceOf(RuntimeException.class ).hasMessage("Upload file fails");


        Assertions.assertThat(userJpaRepository.findAll().size()).isEqualTo(0);
        Assertions.assertThat(customerJpaRepository.findAll().size()).isEqualTo(0);

    }

}
