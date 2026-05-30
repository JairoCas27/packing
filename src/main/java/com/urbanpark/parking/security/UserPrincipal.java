package com.urbanpark.parking.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final String userId;
    private final String email;
    private final String role;
    private final Long tenantId;
    private final String password;
    private final boolean active;
    private final boolean saasUser;

    public UserPrincipal(String userId, String email, String role, Long tenantId,
                         String password, boolean active, boolean saasUser) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.tenantId = tenantId;
        this.password = password;
        this.active = active;
        this.saasUser = saasUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
