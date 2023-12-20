package org.effective_mobile.task_management_system.security;

import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.exception.InvalidAuthTokenException;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.auth.cookieName}")
    private String tokenName;

    public boolean isAuthenticated() throws InvalidAuthTokenException {
        HttpServletRequest request = contextComponent.getRequest();
        String token = authTokenComponent.getTokenFromCookies(request);
        if (token != null) {
            authTokenComponent.validateToken(token);
        } else {
            throw new InvalidAuthTokenException("There is no '%s' in cookies".formatted(tokenName));
        }
        return true;
    }
}

