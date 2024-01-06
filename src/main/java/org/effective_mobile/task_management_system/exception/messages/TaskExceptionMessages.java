package org.effective_mobile.task_management_system.exception.messages;

import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.jetbrains.annotations.NotNull;

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

    public static String sameExecutorChange(Long taskId, String newExecutorUsername) {
        return getMessage("exception.task.executor.same", SIMPLE_NAME, taskId, newExecutorUsername);
    }

    public static String statusCantBeAssign() {
        return ExceptionMessages.getMessage("exception.task.status.assign", Status.ASSIGNED);
    }
    public static String statusCantBeInitial() {
        return ExceptionMessages.getMessage("exception.task.status.initial", Status.NEW);
    }

    public static String impossibleStatusChange(
        Long taskId, Status currentStatus, @NotNull Status newStatus
    ) {
        return ExceptionMessages.getMessage(
            "exception.task.status.change",
            currentStatus,
            SIMPLE_NAME,
            taskId,
            newStatus);
    }
}
