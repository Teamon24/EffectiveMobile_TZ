package org.effective_mobile.task_management_system.exception.messages;

import org.effective_mobile.task_management_system.enums.Status;
import org.springframework.scheduling.config.Task;

import static org.effective_mobile.task_management_system.exception.messages.ExceptionMessages.getMessage;

public final class TaskExceptionMessages {

    private static final String SIMPLE_NAME = Task.class.getSimpleName();

    public static String nothingToChange(Long taskId) {
        return getMessage("exception.task.update.nothing", SIMPLE_NAME, taskId);
    }

    public static String hasNoExecutor(Long id) {
        return getMessage("exception.task.executor.absent", SIMPLE_NAME, id);
    }

    public static String sameStatusChange(Long taskId, Status newStatus) {
        return getMessage("exception.task.status.same", SIMPLE_NAME, taskId, newStatus);
    }
}
