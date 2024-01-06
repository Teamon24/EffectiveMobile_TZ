package org.effective_mobile.task_management_system.security.authorization;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public interface AuthorizationComponent {
    boolean currentUserIsCreator(Long taskId);
    Set<GrantedAuthority> getAuthorities(RequiredAuthorizationInfo requiredAuthorizationInfo);
    RequiredAuthorizationInfo extractAuthorizationInfo(HttpServletRequest request);
}
