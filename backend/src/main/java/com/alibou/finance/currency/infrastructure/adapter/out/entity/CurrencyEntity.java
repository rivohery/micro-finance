package com.alibou.finance.currency.infrastructure.adapter.out.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="currencies")
public class CurrencyEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String code;

    private boolean enable;
}
