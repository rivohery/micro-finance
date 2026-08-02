package com.alibou.finance.auth.infrastructure.model;

import com.alibou.finance.auth.domain.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails, Principal {

    @Getter
    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().value().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword().value();
    }

    @Override
    public String getName() {
        return user.getUsername().value();
    }

    @Override
    public String getUsername() {
        return user.getUsername().value();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnable();
    }
}
