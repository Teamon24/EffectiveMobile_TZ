package org.effective_mobile.task_management_system.security.authorization.privilege;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.database.entity.Privilege;
import org.effective_mobile.task_management_system.database.repository.PrivilegeRepository;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.security.authorization.RequiredAuthorizationInfo;
import org.effective_mobile.task_management_system.utils.enums.UserRole;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
        Collection<UserRole> userRoles = requiredAuthorizationInfo.getUserRoles();
        List<String> rolesNames = ValuableEnum.values(userRoles);
        HashSet<Privilege> privileges = privilegeRepository.findByUserRoles(rolesNames);
        return privileges;
    }

    @Override
    public Boolean checkPrivileges(
        CustomUserDetails customUserDetails,
        Set<String> privileges
    ) {
        return customUserDetails
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet())
            .containsAll(privileges);
    }
}
