package com.alibou.finance.customer.infrastructure.adapter.out.persistence.entity;

import com.alibou.finance.auth.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.alibou.finance.customer.domain.model.CustomerStatus;
import com.alibou.finance.shared.infrastructure.entity.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(name ="customers")
public class CustomerEntity extends BaseAuditingEntity {

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cin;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerStatus status;
    @Column(name = "address_value")
    private String addressValue;

    @Column(name = "address_city")
    private String addressCity;

    @Column(name = "address_zip_code")
    private String addressZipCode;

    @Column(name = "address_country")
    private String addressCountry;
    @Column(nullable = false)
    private String occupation;
    @Column(nullable = false)
    private String imageUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity userEntity;


}
