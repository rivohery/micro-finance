package com.alibou.finance.customer.infrastructure.config;

import com.alibou.finance.customer.application.usecase.*;
import com.alibou.finance.customer.application.service.*;
import com.alibou.finance.customer.domain.out.linker.UserLinkerPort;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerUseCaseBeanConfig {

    @Bean
    public CreateCustomerUseCase createCustomerUseCase(
            CustomerRepository customerRepository,
            UserLinkerPort userLinkerPort,
            FileStoragePort fileStoragePort
    ){
        return new CreateCustomerServiceApplication(customerRepository, userLinkerPort, fileStoragePort);
    }

    @Bean
    public UpdateCustomerUseCase updateCustomerUseCase(
            CustomerRepository customerRepository,
            FileStoragePort fileStoragePort
    ){
        return new UpdateCustomerServiceApplication(customerRepository, fileStoragePort);
    }
    @Bean
    public CustomerLifeCycleUseCase customerLifeCycleUseCase(
            CustomerRepository customerRepository,
            UserLinkerPort userLinkerPort
    ){
        return new CustomerLifeCycleServiceApplication(customerRepository, userLinkerPort);
    }

    @Bean
    public CustomerConsultationUseCase customerConsultationUseCase(
            CustomerRepository customerRepository,
            UserLinkerPort userLinkPort
    ){
        return new CustomerConsultationServiceApplication(customerRepository, userLinkPort);
    }
    @Bean
    public UpdateEmailCustomerUseCase updateEmailCustomerUseCase(CustomerRepository customerRepository){
        return new UpdateEmailCustomerServiceApplication(customerRepository);
    }
    @Bean
    public CustomerStatisticUseCase customerStatisticUseCase(CustomerRepository customerRepository){
        return new CustomerStatisticServiceApplication(customerRepository);
    }
}
