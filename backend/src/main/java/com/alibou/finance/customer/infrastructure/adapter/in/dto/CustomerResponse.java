package com.alibou.finance.customer.infrastructure.adapter.in.dto;

import com.alibou.finance.customer.domain.agregate.CustomerStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private UUID userId;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;
    private String cin;
    private CustomerStatus status;
    private String addressValue;
    private String addressCity;
    private String addressZipCode;
    private String addressCountry;
    private String occupation;
    private byte[] photo;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    private UUID createdBy;
    private UUID lastModifiedBy;

}
