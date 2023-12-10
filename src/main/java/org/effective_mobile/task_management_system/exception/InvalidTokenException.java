package org.effective_mobile.task_management_system.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(JWTVerificationException verificationEx) {
        super(verificationEx);
    }
}
