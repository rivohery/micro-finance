package com.alibou.finance.customer.application.service;

import com.alibou.finance.customer.domain.vo.Address;
import com.alibou.finance.customer.application.command.UpdateCustomerCommand;
import com.alibou.finance.customer.application.usecase.UpdateCustomerUseCase;
import com.alibou.finance.customer.domain.exception.CustomerNotFoundException;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateCustomerServiceApplication implements UpdateCustomerUseCase {

    private static final String SUB_PATH = "clients";
    private final CustomerRepository customerRepository;
    private final FileStoragePort fileStoragePort;

    @Override
    public Customer execute(UpdateCustomerCommand cmd, byte[] contentFile, String fileName) {
        Customer customer = loadCustomer(cmd.customerId());

        if (hasFile(contentFile, fileName)) {
            String uploadedUrl = fileStoragePort.uploadFile(contentFile, fileName, SUB_PATH);
            customer.updateImageUrl(uploadedUrl);
        }

        // Délégation au domaine : le domaine décide quoi modifier
        customer.updateProfile(
                cmd.firstName(),
                cmd.lastName(),
                cmd.occupation(),
                cmd.phoneNumber(),
                cmd.dateOfBirth(),
                new Address(
                        cmd.addressValue(),
                        cmd.addressCity(),
                        cmd.addressZipCode(),
                        cmd.addressCountry()
                )
        );

        return customerRepository.save(customer);
    }

    private Customer loadCustomer(CustomerId customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Client introuvable: identifiant client invalide"
                ));
    }

    private boolean hasFile(byte[] content, String fileName) {
        return content != null && content.length > 0 && fileName != null;
    }

}
