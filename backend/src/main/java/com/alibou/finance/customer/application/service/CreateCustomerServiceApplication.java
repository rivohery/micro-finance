package com.alibou.finance.customer.application.service;

import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.customer.application.port.CreateCustomerUseCase;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.shared.domain.IllegalArgumentException;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateCustomerServiceApplication implements CreateCustomerUseCase {

    private static final String SUB_PATH = "clients";
    private final CustomerRepository customerRepository;
    private final UserUseCase userUseCase;
    private final FileStoragePort fileStoragePort;


    @Override
    public Customer execute(Customer customer, byte[] contentFile, String fileName) {
        if(contentFile == null || contentFile.length == 0){
            throw new IllegalArgumentException("Photo client obligatoire");
        }
        boolean existsByCin = customerRepository.existsByCin(customer.getCin().value());
        if(existsByCin){
            throw new OperationNotPermittedException("Création interrompue: le numéros CIN est déjà utilisé");
        }
        customer.initCustomer();

        var dbuser = userUseCase.create(customer.getUser());

        updatePhoto(customer, contentFile, fileName, SUB_PATH);
        customer.updateUser(dbuser);
        return customerRepository.save(customer);
    }

    private void updatePhoto(Customer customer, byte[] contentFile, String fileName, String subPath) {
        if (contentFile != null && fileName != null) {
            String uploadFileUrl = fileStoragePort.uploadFile(contentFile, fileName, subPath);
            customer.updateImageUrl(uploadFileUrl);
        }
    }
}
