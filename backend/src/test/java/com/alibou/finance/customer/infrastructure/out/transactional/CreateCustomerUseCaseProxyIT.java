package com.alibou.finance.customer.infrastructure.out.transactional;

import com.alibou.finance.BaseServiceIT;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.auth.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.alibou.finance.customer.application.command.CreateCustomerCommand;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.repository.CustomerJpaRepository;
import com.alibou.finance.customer.infrastructure.transactional.CreateCustomerUseCaseProxy;
import com.alibou.finance.shared.domain.vo.user.Email;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.time.LocalDate;

import static org.mockito.Mockito.*;


public class CreateCustomerUseCaseProxyIT extends BaseServiceIT {

    @Autowired
    private CustomerJpaRepository customerJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

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
    CreateCustomerCommand createCustomerCommand;

    @BeforeEach
    void setUp(){
        customerJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
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
    @DisplayName("Tester la transaction sur CreateCustomerUseCaseProxy dans la création d'un client si une exception est levée")
    void should_rollBackUserRegistrationIfUploadFileFails(){
        when(mockBcrypt.encode(anyString())).thenReturn("1234");
        when(fileStoragePort.uploadFile(fileContent, fileName, "clients"))
                .thenThrow(new RuntimeException("Upload file fails"));

        assertThatThrownBy(
                () -> createCustomerUseCaseProxy.execute(createCustomerCommand,fileContent, fileName)
        ).isInstanceOf(RuntimeException.class ).hasMessage("Upload file fails");


        Assertions.assertThat(userJpaRepository.findAll().size()).isEqualTo(0);
        Assertions.assertThat(customerJpaRepository.findAll().size()).isEqualTo(0);

    }

}
