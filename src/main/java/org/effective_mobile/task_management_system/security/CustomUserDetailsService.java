package org.effective_mobile.task_management_system.security;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.database.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserComponent userComponent;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) {
        final User user = userComponent.getByEmail(email);
        return new CustomUserDetails(user);
    }
}