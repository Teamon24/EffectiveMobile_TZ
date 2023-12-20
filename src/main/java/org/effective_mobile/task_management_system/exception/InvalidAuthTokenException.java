package org.effective_mobile.task_management_system.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;

public final class InvalidAuthTokenException extends Exception {
    public InvalidAuthTokenException(JWTVerificationException verificationEx) {
        super(verificationEx);
    }

    public InvalidAuthTokenException(String message) {
        super(message);
    }
}
