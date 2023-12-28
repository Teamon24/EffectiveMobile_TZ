package org.effective_mobile.task_management_system.exception.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;

public final class TokenAuthenticationException extends AuthenticationException {
    public TokenAuthenticationException(JWTVerificationException verificationEx) {
        super(verificationEx);
    }

    public TokenAuthenticationException(String message) {
        super(message);
    }
}
