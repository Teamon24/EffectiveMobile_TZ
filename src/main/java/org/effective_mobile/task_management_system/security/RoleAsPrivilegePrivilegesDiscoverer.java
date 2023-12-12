package org.effective_mobile.task_management_system.security;

import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
@Component
public class RoleAsPrivilegePrivilegesDiscoverer extends PrivilegesDiscoverer {
    @Override
    public List<String> getPrivileges(Role role) {
        return List.of(role.getName().name());
    }
}
