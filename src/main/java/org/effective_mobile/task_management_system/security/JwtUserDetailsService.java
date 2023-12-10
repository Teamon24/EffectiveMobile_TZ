package org.effective_mobile.task_management_system.security;

import jakarta.persistence.EntityNotFoundException;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    private PermissionService permissionsService;

    public JwtUserDetailsService(
        UserRepository userRepository,
        PermissionService permissionsService
    ) {
        this.userRepository = userRepository;
        this.permissionsService = permissionsService;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) {
        final User user = userRepository
            .findByEmail(email)
            .orElseThrow(() -> {
                String message = String.format("User (email = '%s') was not found", email);
                return new EntityNotFoundException(message);
            });

        final List<SimpleGrantedAuthority> permissions = permissionsService.getPermissions(user);
        return new JwtUserDetails(user, permissions);
    }

}