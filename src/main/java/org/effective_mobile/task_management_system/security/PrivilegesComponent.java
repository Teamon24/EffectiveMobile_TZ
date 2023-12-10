package org.effective_mobile.task_management_system.security;

import org.effective_mobile.task_management_system.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PrivilegesComponent {
    public boolean canGetUsers() {
        JwtUserDetails authentication =
            (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean canGetUsers = authentication
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(it -> it.equals(UserRole.USER.name()));

        System.out.println("can get users: " + canGetUsers);
        return canGetUsers;
    }
}

