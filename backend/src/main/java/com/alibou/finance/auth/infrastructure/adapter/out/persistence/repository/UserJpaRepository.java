package com.alibou.finance.auth.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.auth.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Page<UserEntity>findAllByUsernameContainingIgnoreCase(String username, Pageable pageable);

    @Query("""
        Select ue from  UserEntity ue 
        where lower(ue.username) like lower(concat(:username, '%'))
        and ue.role != 'CLIENT'
    """)
    Page<UserEntity>searchAllEmployeeByUsernameStart(String username, Pageable pageable);
    Optional<UserEntity>findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Modifying(clearAutomatically = true)//pour dire à hibernate de toujours nettoyer son cache
    @Query("""
        Update UserEntity u set u.password =:password where u.id =:userId
    """)
    void changePassword(@Param("userId") UUID userId,@Param("password") String password);

    @Modifying(clearAutomatically = true)
    @Query("""
        Update UserEntity u set u.enable = false where u.id =:userId
    """)
    void disableUser(@Param("userId")UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("""
        Update UserEntity u set u.enable =:status where u.id =:userId
    """)
    void changeStatus(@Param("userId")UUID userId, @Param("status")boolean status);
}
