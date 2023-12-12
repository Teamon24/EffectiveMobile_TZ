package org.effective_mobile.task_management_system.exception;

public class IllegalStatusChangeException extends RuntimeException {
    public IllegalStatusChangeException() { super(); }
    public IllegalStatusChangeException(String message) {
        super(message);
    }
}
