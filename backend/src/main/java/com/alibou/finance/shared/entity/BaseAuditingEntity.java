package com.alibou.finance.shared.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    protected UUID id;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    protected LocalDate createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    protected LocalDate lastModifiedDate;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    protected UUID createdBy;

    @LastModifiedBy
    @Column(insertable = false)
    protected UUID lastModifiedBy;
}

