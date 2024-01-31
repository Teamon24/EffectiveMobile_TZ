package org.effective_mobile.task_management_system.security.authorization.privilege;

import org.effective_mobile.task_management_system.database.entity.Privilege;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.security.authorization.RequiredAuthorizationInfo;

import java.util.Set;

public interface PrivilegesComponent {
    Set<Privilege> getPrivileges(RequiredAuthorizationInfo requiredAuthorizationInfo);
    Boolean checkPrivileges(CustomUserDetails customUserDetails, Set<String> privileges);
}
