package com.alibou.finance.customer.application.service;

import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.customer.application.command.CreateCustomerCommand;
import com.alibou.finance.customer.application.usecase.CreateCustomerUseCase;
import com.alibou.finance.customer.application.utils.CustomerFactory;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.linker.UserLinkerPort;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.customer.domain.vo.Cin;
import com.alibou.finance.customer.domain.vo.CustomerPhoto;
import com.alibou.finance.customer.domain.vo.PhoneNumber;
import com.alibou.finance.shared.domain.exception.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateCustomerServiceApplication implements CreateCustomerUseCase {

    private static final String SUB_PATH = "clients";
    private final CustomerRepository customerRepository;
    private final UserLinkerPort userLinkerPort;
    private final FileStoragePort fileStoragePort;


    @Override
    public Customer execute(CreateCustomerCommand command, byte[] contentFile, String fileName) {
        CustomerPhoto photo = new CustomerPhoto(contentFile, fileName);

        ensureCinIsAvailable(command.cin());
        ensurePhoneNumberNoDuplicated(command.phoneNumber());

        UserInfo dbuser = userLinkerPort.createUserFromCustomerInfo(
                command.username(), command.email()
        );

        Customer customer = CustomerFactory.create(command, dbuser);
        customer.initCustomer();

        String imageUrl = fileStoragePort.uploadFile(contentFile, fileName, SUB_PATH);
        customer.updateImageUrl(imageUrl);

        return customerRepository.save(customer);
    }

    private void ensureCinIsAvailable(Cin cin) {
        if (customerRepository.existsByCin(cin)) {
            throw new OperationNotPermittedException(
                    "Création interrompue: le numéro CIN est déjà utilisé"
            );
        }
    }

    private void ensurePhoneNumberNoDuplicated(PhoneNumber phoneNumber) {
        if (customerRepository.existsByPhoneNumber(phoneNumber)) {
            throw new OperationNotPermittedException(
                    "Création interrompue: le numéro du téléphone est déjà utilisé"
            );
        }
    }


}
