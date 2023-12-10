package org.effective_mobile.task_management_system.exception;

public class SuchUserAlreadyExistsException extends RuntimeException {
    public SuchUserAlreadyExistsException(String message) {
        super(message);
    }
}
