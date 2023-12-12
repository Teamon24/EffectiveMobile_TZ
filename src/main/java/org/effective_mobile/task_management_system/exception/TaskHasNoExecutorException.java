package org.effective_mobile.task_management_system.exception;

public class TaskHasNoExecutorException extends RuntimeException {
    public TaskHasNoExecutorException(String message) {
        super(message);
    }
}
