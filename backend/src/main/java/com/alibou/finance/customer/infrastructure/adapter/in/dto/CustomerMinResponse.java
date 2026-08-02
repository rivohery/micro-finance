package com.alibou.finance.customer.infrastructure.adapter.in.dto;

import com.alibou.finance.customer.domain.model.CustomerStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMinResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private byte[] photo;
    private LocalDate dateOfBirth;
    private CustomerStatus status;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
}
