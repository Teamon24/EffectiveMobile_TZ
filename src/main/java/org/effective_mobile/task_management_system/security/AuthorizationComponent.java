package org.effective_mobile.task_management_system.security;

import org.effective_mobile.task_management_system.database.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;

public interface AuthorizationComponent {
    boolean currentUserIsCreator(Long taskId);
    HashSet<GrantedAuthority> getAuthorities(User user);
}
