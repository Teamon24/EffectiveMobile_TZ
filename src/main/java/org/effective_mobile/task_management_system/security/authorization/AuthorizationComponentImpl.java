package org.effective_mobile.task_management_system.security.authorization;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.database.entity.Privilege;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.security.ContextComponent;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.security.authorization.privilege.PrivilegesComponent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static org.effective_mobile.task_management_system.maintain.cache.AppCacheNames.AUTHORITIES;

@Component
@AllArgsConstructor
public class AuthorizationComponentImpl implements AuthorizationComponent {

    private UserComponent userComponent;
    private ContextComponent contextComponent;
    private TaskComponent taskComponent;
    private PrivilegesComponent privilegesComponent;

    @Override
    public boolean currentUserIsCreator(Long taskId) {
        Task task = taskComponent.getTask(taskId);
        CustomUserDetails principal = contextComponent.getPrincipal();
        userComponent.checkUserIsCreator(principal, task);
        return true;
    }

    @Override
    @Cacheable(cacheNames = AUTHORITIES, key = "#requiredAuthorizationInfo.getUserId()")
    public Set<GrantedAuthority> getAuthorities(RequiredAuthorizationInfo requiredAuthorizationInfo) {
        Set<Privilege> privileges = privilegesComponent.getPrivileges(requiredAuthorizationInfo);

        Set<GrantedAuthority> authorities =
            privileges
                .stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toSet());

        requiredAuthorizationInfo
            .getUserRoles()
            .forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getValue())));

        return authorities;
    }

    @Override
    public RequiredAuthorizationInfo extractAuthorizationInfo(HttpServletRequest request) {
        return contextComponent.getPrincipal();
    }
}

