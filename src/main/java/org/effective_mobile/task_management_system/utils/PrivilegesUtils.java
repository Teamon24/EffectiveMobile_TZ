package org.effective_mobile.task_management_system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.enums.UserRole;
import org.effective_mobile.task_management_system.security.JwtPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrivilegesUtils {

    public static boolean hasAnyRoles(
        JwtPrincipal authentication,
        Collection<UserRole> expectedRoles
    ) {
        List<String> rolesNames = expectedRoles.stream().map(Enum::name).toList();
        return authentication
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(rolesNames::contains);
    }
}
