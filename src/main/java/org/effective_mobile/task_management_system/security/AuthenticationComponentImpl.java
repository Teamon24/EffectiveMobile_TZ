package org.effective_mobile.task_management_system.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.exception.auth.TokenAuthenticationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthenticationComponentImpl implements AuthenticationComponent {

    private final AuthTokenComponent authTokenComponent;
    private final ContextComponent contextComponent;

    @Override
    public boolean isAuthenticated() throws TokenAuthenticationException {
        HttpServletRequest request = contextComponent.getRequest();
        String token = authTokenComponent.getTokenFromCookies(request);
        authTokenComponent.validateToken(token);
        return true;
    }
}
