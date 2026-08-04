package com.alibou.finance.customer.infrastructure.adapter.in.dto;

import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.Address;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.shared.vo.domain.Email;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record CustomerRequest(
        String firstName,
        String lastName,
        UUID userId,
        String username,

        //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) :Pour spring pour verifier le format
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")//pour verifier le format de la date (ISO)
        LocalDate dateOfBirth,
        String phoneNumber,
        String email,
        String cin,
        String occupation,
        String addressValue,
        String addressCity,
        String addressZipCode,
        String addressCountry
) {

    public static Customer toDomain(CustomerRequest request) {
        User user;
        if(request.userId() == null){
            user = User.create(request.username(), request.email(), RoleEnum.CLIENT);
        } else {
            user = User.update(request.userId(), request.username(), request.email());
        }
        return Customer.builder()
                .user(user)
                .phoneNumber(new PhoneNumber(request.phoneNumber()))
                .firstName(new FirstName(request.firstName()))
                .dateOfBirth(new DateOfBirth(request.dateOfBirth()))
                .lastName(new LastName(request.lastName()))
                .email(new Email(request.email()))
                .cin(new Cin(request.cin()))
                .address(new Address(request.addressValue(), request.addressCity(), request.addressZipCode(), request.addressCountry()))
                .occupation(new Occupation(request.occupation()))
                .build();

    }

}
