package org.effective_mobile.task_management_system.security.authorization;

import org.effective_mobile.task_management_system.utils.enums.UserRole;

import java.util.Collection;

public interface RequiredAuthorizationInfo {
    Long getUserId();
    Collection<UserRole> getUserRoles();
}
