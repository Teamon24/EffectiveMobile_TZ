package org.effective_mobile.task_management_system.security.authentication;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.exception.auth.TokenAuthenticationException;
import org.effective_mobile.task_management_system.security.ContextComponent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthenticationComponentImpl implements AuthenticationComponent {

    private final AuthenticationTokenComponent authenticationTokenComponent;
    private final ContextComponent contextComponent;

    @Override
    public boolean isAuthenticated() throws TokenAuthenticationException {
        authenticationTokenComponent.validateToken(contextComponent.getRequest());
        return true;
    }
}
