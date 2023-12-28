package org.effective_mobile.task_management_system.security;

import lombok.Getter;
import lombok.Setter;
import org.effective_mobile.task_management_system.database.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

public class CustomUserDetails implements UserDetails {

    @Getter private final Long userId;
    @Getter private final String password;
    @Getter private final String email;
    @Getter private final String usernameAtDb;
    private final String detailsUsername;

    @Getter
    @Setter
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user, Function<User, String> usernameProviding, HashSet<GrantedAuthority> authorities) {
        this.userId = user.getId();
        this.usernameAtDb = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.detailsUsername = usernameProviding.apply(user);
        this.authorities = authorities;
    }

    @Override public String getUsername() { return detailsUsername; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}