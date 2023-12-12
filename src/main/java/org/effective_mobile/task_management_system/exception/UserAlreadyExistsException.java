package org.effective_mobile.task_management_system.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() { super(); }
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
