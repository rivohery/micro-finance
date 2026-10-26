package com.alibou.finance.customer.infrastructure.adapter.out.mapper;

import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.customer.domain.vo.Address;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.customer.application.command.CreateCustomerCommand;
import com.alibou.finance.customer.application.command.UpdateCustomerCommand;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.customer.infrastructure.adapter.in.dto.CustomerMinResponse;
import com.alibou.finance.customer.infrastructure.adapter.in.dto.CustomerRequest;
import com.alibou.finance.customer.infrastructure.adapter.in.dto.CustomerResponse;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import com.alibou.finance.customer.infrastructure.utils.FileUtils;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.domain.vo.user.Email;

import java.util.UUID;

public class CustomerMapper {

    public static CreateCustomerCommand dtoToCreateCustomerCommand(CustomerRequest request){
        return new CreateCustomerCommand(
              new FirstName(request.firstName()),
              new LastName(request.lastName()),
              new Username(request.username()),
              new DateOfBirth(request.dateOfBirth()),
              new PhoneNumber(request.phoneNumber()),
              new Email(request.email()),
              new Cin(request.cin()),
              new Occupation(request.occupation()),
              request.addressValue(),
              request.addressCity(),
              request.addressZipCode(),
              request.addressCountry()
        );
    }

    public static UpdateCustomerCommand dtoToUpdateCustomerCommand(CustomerRequest request, UUID customerId){
        return new UpdateCustomerCommand(
                new CustomerId(customerId),
                new UserId(request.userId()),
                new FirstName(request.firstName()),
                new LastName(request.lastName()),
                new DateOfBirth(request.dateOfBirth()),
                new PhoneNumber(request.phoneNumber()),
                new Cin(request.cin()),
                new Occupation(request.occupation()),
                request.addressValue(),
                request.addressCity(),
                request.addressZipCode(),
                request.addressCountry()
        );
    }

    public static CustomerEntity domainToEntity(Customer customer){
        return CustomerEntity.builder()
                .id(customer.getCustomerId().value())
                .addressCity(customer.getAddress().city())
                .addressCountry(customer.getAddress().country())
                .addressValue(customer.getAddress().value())
                .addressZipCode(customer.getAddress().zipCode())
                .cin(customer.getCin().value())
                .dateOfBirth(customer.getDateOfBirth().value())
                .firstName(customer.getFirstName().value())
                .imageUrl(customer.getImageUrl().value())
                .lastName(customer.getLastName().value())
                .occupation(customer.getOccupation().value())
                .phoneNumber(customer.getPhoneNumber().value())
                .email(customer.getEmail().value())
                .status(customer.getCustomerStatus().value())
                .userId(customer.getUserId().value())
                .build();
    }

    public static Customer entityToDomain(CustomerEntity customerEntity){
        var address = new Address(
                customerEntity.getAddressValue(),
                customerEntity.getAddressCity(),
                customerEntity.getAddressZipCode(),
                customerEntity.getAddressCountry()
        );
        return Customer.builder()
                .address(address)
                .createdBy(customerEntity.getCreatedBy())
                .createdDate(customerEntity.getCreatedDate())
                .lastModifiedBy(customerEntity.getLastModifiedBy())
                .lastModifiedDate(customerEntity.getLastModifiedDate())
                .dateOfBirth(DateOfBirth.from(customerEntity.getDateOfBirth()))
                .customerId(CustomerId.from(customerEntity.getId()))
                .cin(new Cin(customerEntity.getCin()))
                .firstName(new FirstName(customerEntity.getFirstName()))
                .lastName(new LastName(customerEntity.getLastName()))
                .imageUrl(new ImageUrl(customerEntity.getImageUrl()))
                .occupation(new Occupation(customerEntity.getOccupation()))
                .phoneNumber(new PhoneNumber(customerEntity.getPhoneNumber()))
                .email(new Email(customerEntity.getEmail()))
                .customerStatus(new CustomerStatus(customerEntity.getStatus()))
                .userId(new UserId(customerEntity.getUserId()))
                .build();
    }

    public static CustomerResponse domainToCustomerFullResponse(Customer customer, UserInfo user){
        return CustomerResponse.builder()
                .id(customer.getCustomerId().value())
                .addressCity(customer.getAddress().city())
                .addressCountry(customer.getAddress().country())
                .addressValue(customer.getAddress().value())
                .addressZipCode(customer.getAddress().zipCode())
                .cin(customer.getCin().value())
                .dateOfBirth(customer.getDateOfBirth().value())
                .email(customer.getEmail().value())
                .firstName(customer.getFirstName().value())
                .photo(FileUtils.readFileFromLocation(customer.getImageUrl().value()))
                .lastName(customer.getLastName().value())
                .username(user.username().value())
                .userId(user.userId().value())
                .occupation(customer.getOccupation().value())
                .phoneNumber(customer.getPhoneNumber().value())
                .status(customer.getCustomerStatus().value())
                .createdDate(customer.getCreatedDate())
                .createdBy(customer.getCreatedBy())
                .lastModifiedDate(customer.getLastModifiedDate())
                .lastModifiedBy(customer.getLastModifiedBy())
                .build();
    }

    public static CustomerMinResponse domainToMinResponse(Customer customer){
        return CustomerMinResponse.builder()
                .id(customer.getCustomerId().value())
                .firstName(customer.getFirstName().value())
                .lastName(customer.getLastName().value())
                .email(customer.getEmail().value())
                .phoneNumber(customer.getPhoneNumber().value())
                .photo(FileUtils.readFileFromLocation(customer.getImageUrl().value()))
                .dateOfBirth(customer.getDateOfBirth().value())
                .status(customer.getCustomerStatus().value())
                .createdDate(customer.getCreatedDate())
                .lastModifiedDate(customer.getLastModifiedDate())
                .build();

    }
}
