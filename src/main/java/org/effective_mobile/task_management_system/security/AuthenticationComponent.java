package org.effective_mobile.task_management_system.security;

import org.effective_mobile.task_management_system.exception.auth.TokenAuthenticationException;

public interface AuthenticationComponent {
    boolean isAuthenticated() throws TokenAuthenticationException;
}
