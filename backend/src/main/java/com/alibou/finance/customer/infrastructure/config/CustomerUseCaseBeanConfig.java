package com.alibou.finance.customer.infrastructure.config;

import com.alibou.finance.account.application.port.usecase.AccountConsultationUseCase;
import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.customer.application.port.CreateCustomerUseCase;
import com.alibou.finance.customer.application.port.CustomerConsultationUseCase;
import com.alibou.finance.customer.application.port.CustomerLifeCycleUseCase;
import com.alibou.finance.customer.application.port.UpdateCustomerUseCase;
import com.alibou.finance.customer.application.service.CreateCustomerServiceApplication;
import com.alibou.finance.customer.application.service.CustomerConsultationServiceApplication;
import com.alibou.finance.customer.application.service.CustomerLifeCycleServiceApplication;
import com.alibou.finance.customer.application.service.UpdateCustomerServiceApplication;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerUseCaseBeanConfig {

    @Bean
    public CreateCustomerUseCase buildCreateCustomerUseCaseBean(
            CustomerRepository customerRepository,
            UserUseCase userUseCase,
            FileStoragePort fileStoragePort
    ){
        return new CreateCustomerServiceApplication(customerRepository, userUseCase, fileStoragePort);
    }

    @Bean
    public UpdateCustomerUseCase buildUpdateCustomerUseCaseBean(
            CustomerRepository customerRepository,
            UserUseCase userUseCase,
            FileStoragePort fileStoragePort
    ){
        return new UpdateCustomerServiceApplication(customerRepository, userUseCase, fileStoragePort);
    }
    @Bean
    public CustomerLifeCycleUseCase buildCustomerLifeCycleUseCase(CustomerRepository customerRepository, UserUseCase userUseCase){
        return new CustomerLifeCycleServiceApplication(customerRepository, userUseCase);
    }

    @Bean
    public CustomerConsultationUseCase buildCustomerConsultationUseCase(CustomerRepository customerRepository, AccountConsultationUseCase accountConsultationService){
        return new CustomerConsultationServiceApplication(customerRepository, accountConsultationService);
    }
}
