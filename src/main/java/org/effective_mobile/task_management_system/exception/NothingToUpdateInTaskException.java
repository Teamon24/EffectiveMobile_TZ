package org.effective_mobile.task_management_system.exception;

public class NothingToUpdateInTaskException extends RuntimeException {
    public NothingToUpdateInTaskException() { super(); }
    public NothingToUpdateInTaskException(String message) {
        super(message);
    }
}
