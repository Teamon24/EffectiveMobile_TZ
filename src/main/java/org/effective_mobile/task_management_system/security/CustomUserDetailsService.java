package org.effective_mobile.task_management_system.security;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) {
        final User user = userRepository
            .findByEmail(email)
            .orElseThrow(() -> {
                String message = String.format("User (email = '%s') was not found", email);
                return new EntityNotFoundException(message);
            });

        return new CustomUserDetails(user);
    }

}