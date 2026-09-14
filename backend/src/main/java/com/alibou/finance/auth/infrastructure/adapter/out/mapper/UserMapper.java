package com.alibou.finance.auth.infrastructure.adapter.out.mapper;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.shared.vo.domain.Email;
import com.alibou.finance.auth.domain.vo.Password;
import com.alibou.finance.auth.domain.vo.Role;
import com.alibou.finance.auth.domain.vo.Username;
import com.alibou.finance.auth.infrastructure.adapter.in.dto.UserResponse;
import com.alibou.finance.auth.infrastructure.adapter.out.persistence.entity.UserEntity;

import java.util.Objects;

public class UserMapper {

    public static UserResponse domainToDto(User user){
        if(Objects.isNull(user)){
            throw new RuntimeException("Objet User nulle dans {mapToDto}");
        }
        return UserResponse.builder()
                .email(user.getEmail().value())
                .username(user.getUsername().value())
                .role(user.getRole().value())
                .enable(user.isEnable())
                .id(user.getUserId().value())
                .build();
    }

    public static User entityToDomain(UserEntity entity){
        if(Objects.isNull(entity)){
            throw new RuntimeException("Objet UserEntity nulle dans {entityToDomain}");
        }
        return User.builder()
                .userId(UserId.from(entity.getId()))
                .email(new Email(entity.getEmail()))
                .username(new Username(entity.getUsername()))
                .password(new Password(entity.getPassword()))
                .role(new Role(entity.getRole()))
                .enable(entity.isEnable())
                .build();
    }

    public static UserEntity domainToEntity(User user){
        if(Objects.isNull(user)){
            throw new RuntimeException("Objet User nulle dans {mapToEntity}");
        }
        return UserEntity.builder()
                .id(user.getUserId().value())
                .email(user.getEmail().value())
                .password(user.getPassword().value())
                .username(user.getUsername().value())
                .enable(user.isEnable())
                .role(user.getRole().value())
                .build();
    }

}
