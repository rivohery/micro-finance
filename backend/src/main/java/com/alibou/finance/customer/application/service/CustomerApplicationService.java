package com.alibou.finance.customer.application.service;

import com.alibou.finance.account.application.port.usecase.AccountConsultationUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.customer.application.port.CustomerUseCase;
import com.alibou.finance.customer.application.port.DetailCustomerWithAccount;
import com.alibou.finance.customer.domain.exception.CustomerNotFoundException;
import com.alibou.finance.customer.domain.model.Customer;
import com.alibou.finance.customer.domain.out.repository.CustomerRepository;
import com.alibou.finance.customer.domain.model.CustomerStatus;
import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.shared.domain.IllegalArgumentException;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerApplicationService implements CustomerUseCase {
    private final CustomerRepository customerRepository;
    private final UserUseCase userUseCase;
    private final FileStoragePort fileStoragePort;

    private final AccountConsultationUseCase accountConsultationService;

    private static final String SUB_PATH = "clients";

    @Override
    @Transactional
    public Customer create(Customer customer, byte[] fileContent, String fileName) {
        if(fileContent == null || fileContent.length == 0){
            throw new IllegalArgumentException("Photo client obligatoire");
        }
        boolean existsByCin = customerRepository.existsByCin(customer.getCin().value());
        if(existsByCin){
            throw new OperationNotPermittedException("Création interrompue: le numéros CIN est déjà utilisé");
        }
        customer.initCustomer();

        var dbuser = userUseCase.create(customer.getUser());

        updatePhoto(customer, fileContent, fileName, SUB_PATH);
        customer.updateUser(dbuser);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer update(Customer customer, byte[] contentFile, String fileName) {
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

    @Override
    @Transactional(readOnly = true)
    public Customer findCustomerDetailsById(CustomerId customerId) {
        return customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: identifiant customerId invalide")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> findAllEnableCustomerBySearch(String search, Pageable pageable) {
        return customerRepository.fetchAllEnableCustomerBySearchBegin(search, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> findAllCustomerBySearchStart(String search, Pageable pageable) {
        return customerRepository.findAllCustomerBySearchBegin(search, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyIfCustomerIsActive(CustomerId customerId) {
        Customer customer = getCustomerById(customerId);
        return customer.getStatus().value() == CustomerStatus.ACTIVE;
    }

    @Override
    @Transactional
    public boolean CloseCustomerAccount(CustomerId customerId) {
        Customer customer = getCustomerById(customerId);
        customer.close();
        customerRepository.closeAccount(customer);
        userUseCase.disableUser(customer.getUser().getUserId());
        return true;
    }

    @Override
    @Transactional
    public CustomerStatus updateStatusCustomer(CustomerId customerId,  CustomerStatus status) {
        Customer customer = getCustomerById(customerId);

        if(status == CustomerStatus.ACTIVE){
            customer.active();
        } else if(status == CustomerStatus.SUSPENDED) {
            customer.suspend();
        } else {
            throw new OperationNotPermittedException("Modification status invalide");
        }
        return customerRepository.updateCustomerStatus(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public DetailCustomerWithAccount findCustomerWithAccounts(CustomerId customerId) {
        Customer customer = getCustomerById(customerId);

        List<Account>accounts = accountConsultationService.findAllByCustomerId(customerId);
        return DetailCustomerWithAccount.builder()
                .customer(customer)
                .accounts(accounts)
                .build();
    }

    @Override
    public CustomerId findCustomerIdByUser(User user) {
        return customerRepository.findCustomerIdByUser(user).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: aucun client correspond au utilisateur connecté")
        );
    }

    private void updatePhoto(Customer customer, byte[] contentFile, String fileName, String subPath) {
        if (contentFile != null && fileName != null) {
            String uploadFileUrl = fileStoragePort.uploadFile(contentFile, fileName, subPath);
            customer.setImageUrl(uploadFileUrl);
        }
    }

    private Customer getCustomerById(CustomerId customerId){
        return customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Client introuvable: identifiant client invalide")
        );
    }
}
