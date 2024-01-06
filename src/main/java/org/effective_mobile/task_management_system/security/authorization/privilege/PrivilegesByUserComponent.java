package org.effective_mobile.task_management_system.security.authorization.privilege;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.database.entity.Privilege;
import org.effective_mobile.task_management_system.database.repository.PrivilegeRepository;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.security.authorization.RequiredAuthorizationInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.effective_mobile.task_management_system.maintain.cache.AppCacheNames.PRIVILEGES;

@Component
@AllArgsConstructor
public class PrivilegesByUserComponent implements PrivilegesComponent {

    private PrivilegeRepository privilegeRepository;

    @Override
    @Cacheable(cacheNames = PRIVILEGES, key = "#requiredAuthorizationInfo.getUserId()")
    public Set<Privilege> getPrivileges(RequiredAuthorizationInfo requiredAuthorizationInfo) {
        Long userId = requiredAuthorizationInfo.getUserId();
        HashSet<Privilege> privileges = privilegeRepository.findByUserId(userId);
        return privileges;
    }

    @Override
    public Boolean userHasPrivileges(
        CustomUserDetails customUserDetails,
        Set<String> privileges
    ) {
        return customUserDetails
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet()).containsAll(privileges);
    }
}
