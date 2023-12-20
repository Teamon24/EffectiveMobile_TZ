package org.effective_mobile.task_management_system.security;

import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.exception.InvalidTokenException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationComponent {

    private final AuthTokenComponent authTokenComponent;
    private final ContextComponent contextComponent;

    public AuthenticationComponent(
        AuthTokenComponent authTokenComponent,
        ContextComponent contextComponent
    ) {
        this.authTokenComponent = authTokenComponent;
        this.contextComponent = contextComponent;
    }

    public boolean isAuthenticated() throws InvalidTokenException {
        HttpServletRequest request = contextComponent.getRequest();
        String token = authTokenComponent.getTokenFromCookies(request);
        if (token != null) {
            authTokenComponent.validateToken(token);
        } else {
            throw new InvalidTokenException("There is no token in cookies");
        }
        return true;
    }
}

