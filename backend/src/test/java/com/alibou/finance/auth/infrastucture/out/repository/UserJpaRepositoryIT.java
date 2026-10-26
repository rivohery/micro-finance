package com.alibou.finance.auth.infrastucture.out.repository;

import com.alibou.finance.BaseRepositoryIT;
import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.auth.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.alibou.finance.auth.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserJpaRepositoryIT extends BaseRepositoryIT {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp(){
        userJpaRepository.deleteAll();
        UserEntity u1 = createMinUser("alibou", "alibou@gmail.com", RoleEnum.EMPLOYE);
        UserEntity u2 = createMinUser("Ali", "ali@gmail.com", RoleEnum.ADMIN);
        UserEntity u3 = createMinUser("mohamed", "mohamed@gmail.com", RoleEnum.EMPLOYE);
        UserEntity u4 = createMinUser("salah", "salah@gmail.com", RoleEnum.ADMIN);
        UserEntity u5 = createMinUser("alex", "alex@gmail.com", RoleEnum.CLIENT);
        userJpaRepository.saveAll(List.of(u1,u2,u3,u4,u5));
    }
    @Test
    void test_SearchAllEmployeeByUsernameStart(){
        Pageable pageable = PageRequest.of(0,5);
        Page<UserEntity> result = userJpaRepository.searchAllEmployeeByUsernameStart("al", pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getContent()).extracting(ue -> ue.getRole()).containsExactlyInAnyOrder(RoleEnum.ADMIN, RoleEnum.EMPLOYE);
    }

    private UserEntity createMinUser(String username, String email, RoleEnum role){
        return UserEntity.builder()
                .id(UUID.randomUUID())
                .password("0000")
                .email(email)
                .role(role)
                .username(username)
                .enable(true)
                .build();
    }
}
