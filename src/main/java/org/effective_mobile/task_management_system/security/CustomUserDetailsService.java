package org.effective_mobile.task_management_system.security;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.UsernameProvider;
import org.effective_mobile.task_management_system.database.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UsernameProvider usernameProvider;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = usernameProvider.getUserBy(username);
        return new CustomUserDetails(
            user,
            usernameProvider.getUsername(user),
            Sets.newHashSet()
        );
    }
}