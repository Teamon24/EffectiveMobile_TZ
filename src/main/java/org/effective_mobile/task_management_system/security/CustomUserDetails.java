package org.effective_mobile.task_management_system.security;

import lombok.Getter;
import lombok.Setter;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.security.authorization.RequiredAuthorizationInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class CustomUserDetails implements UserDetails, RequiredAuthorizationInfo {

    @Getter private final Long userId;
    @Getter private final String password;
    @Getter private final String email;
    @Getter private final String usernameAtDb;
    private final String detailsUsername;

    @Getter
    @Setter
    private Collection<GrantedAuthority> authorities;

    public CustomUserDetails(
        User user,
        String detailsUsername,
        Set<GrantedAuthority> authorities
    ) {
        this.userId = user.getId();
        this.usernameAtDb = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.detailsUsername = detailsUsername;
        this.authorities = authorities;
    }

    @Override public String getUsername() { return detailsUsername; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}