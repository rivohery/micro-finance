package com.alibou.finance.auth.infrastructure.adapter.out.persistence.entity;

import com.alibou.finance.auth.domain.model.RoleEnum;
import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.auth.domain.service.PasswordHasher;
import com.alibou.finance.auth.domain.vo.Password;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="users")
public class UserEntity {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private boolean enable;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

}
