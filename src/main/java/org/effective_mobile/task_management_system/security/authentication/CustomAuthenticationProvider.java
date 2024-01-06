package org.effective_mobile.task_management_system.security.authentication;

import com.google.common.collect.Sets;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.exception.auth.PasswordAuthenticationException;
import org.effective_mobile.task_management_system.exception.auth.UserAuthenticationException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (EntityNotFoundException e) {
            throw new UserAuthenticationException(e.getMessage());
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new PasswordAuthenticationException("Wrong password");
        }

        return authenticated(password, userDetails);
    }

    private UsernamePasswordAuthenticationToken authenticated(
        String password,
        UserDetails userDetails
    ) {
        return new UsernamePasswordAuthenticationToken(
            userDetails,
            password,
            Sets.newHashSet()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // Return true if this AuthenticationProvider supports the provided authentication class
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}