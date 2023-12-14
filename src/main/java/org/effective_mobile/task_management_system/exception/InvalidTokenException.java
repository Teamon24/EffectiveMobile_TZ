package org.effective_mobile.task_management_system.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;

public final class InvalidTokenException extends Exception {
    public InvalidTokenException(JWTVerificationException verificationEx) {
        super(verificationEx);
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
