package com.alibou.finance.customer.application.service;

import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.customer.application.port.UpdateCustomerUseCase;
import com.alibou.finance.customer.domain.exception.CustomerNotFoundException;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.customer.domain.vo.CustomerId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateCustomerServiceApplication implements UpdateCustomerUseCase {

    private static final String SUB_PATH = "clients";
    private final CustomerRepository customerRepository;
    private final UserUseCase userUseCase;
    private final FileStoragePort fileStoragePort;
    @Override
    public Customer execute(Customer customer, byte[] contentFile, String fileName) {
        Customer dbcustomer = getCustomerById(customer.getCustomerId());

        updatePhoto(dbcustomer, contentFile, fileName, SUB_PATH);
        var dbuser = userUseCase.update(customer.getUser());
        dbcustomer.updateUser(dbuser);
        dbcustomer.updateFirstName(customer.getFirstName());
        dbcustomer.updateLastName(customer.getLastName());
        dbcustomer.updateOccupation(customer.getOccupation());
        dbcustomer.updateAdresse(customer.getAddress());
        dbcustomer.updatePhoneNumber(customer.getPhoneNumber());
        dbcustomer.updateDateOfBirth(customer.getDateOfBirth());
        dbcustomer.updateEmail(customer.getEmail());
        return customerRepository.save(dbcustomer);
    }

    private void updatePhoto(Customer customer, byte[] contentFile, String fileName, String subPath) {
        if (contentFile != null && fileName != null) {
            String uploadFileUrl = fileStoragePort.uploadFile(contentFile, fileName, subPath);
            customer.updateImageUrl(uploadFileUrl);
        }
    }

    private Customer getCustomerById(CustomerId customerId){
        return customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: identifiant client invalide")
        );
    }
}
