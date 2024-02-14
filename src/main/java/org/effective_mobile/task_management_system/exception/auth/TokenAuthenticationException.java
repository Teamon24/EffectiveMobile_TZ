package org.effective_mobile.task_management_system.exception.auth;

public final class TokenAuthenticationException extends AuthenticationException {
    public TokenAuthenticationException(Throwable throwable) {
        super(throwable);
    }

    public TokenAuthenticationException(String message) {
        super(message);
    }
}
