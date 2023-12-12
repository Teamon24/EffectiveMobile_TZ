package org.effective_mobile.task_management_system.security;

import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PrivilegesDiscoverer {
    protected abstract List<String> getPrivileges(Role role);

    public List<GrantedAuthority> getAuthorities(User user) {
        return getGrantedAuthorities(getPrivileges(user.getRoles()));
    }

    private Set<String> getPrivileges(Collection<Role> roles) {
        return Stream.concat(
            getPrivilegesStream(roles),
            getRolesNamesStream(roles)
        ).collect(Collectors.toSet());
    }

    private Stream<String> getRolesNamesStream(Collection<Role> roles) {
        return roles.stream().map(role -> role.getName().name());
    }

    private Stream<String> getPrivilegesStream(Collection<Role> roles) {
        return roles.stream().map(this::getPrivileges).flatMap(Collection::stream);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Set<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }
}
