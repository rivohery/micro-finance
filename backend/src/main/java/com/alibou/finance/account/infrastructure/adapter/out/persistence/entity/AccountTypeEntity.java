package com.alibou.finance.account.infrastructure.adapter.out.persistence.entity;

import com.alibou.finance.shared.entity.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="account_types")
public class AccountTypeEntity extends BaseAuditingEntity {
    @Column(nullable = false, unique = true)
    private String name;//Épargne-courante-business
    @Column(nullable = false, unique = true)
    private String code; // 10=>courante;20=>épargne;30=>business
    @Column(precision = 19, scale = 4)//pour ne pas perdre des données dans la BD
    private BigDecimal accountFee;
    @Column(precision = 19, scale = 4, name = "interest_rate")
    private BigDecimal annualInterestRate;
    @Column(precision = 19, scale = 4)
    private BigDecimal minimumBalance;

}
