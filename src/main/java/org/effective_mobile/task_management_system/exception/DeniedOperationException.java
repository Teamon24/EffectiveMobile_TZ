package org.effective_mobile.task_management_system.exception;

public class DeniedOperationException extends RuntimeException {
    public DeniedOperationException(String message) {
        super(message);
    }
}
