package org.effective_mobile.task_management_system.security;

import lombok.Getter;
import lombok.Setter;
import org.effective_mobile.task_management_system.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class JwtPrincipal implements UserDetails {

    private final Long userId;
    private final String password;
    private final String email;
    private final String usernameAtDb;

    @Setter
    private Collection<? extends GrantedAuthority> authorities;

    public JwtPrincipal(User user, Collection<? extends GrantedAuthority> authorities) {
        this.userId = user.getId();
        this.usernameAtDb = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.authorities = authorities;
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
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}