package org.effective_mobile.task_management_system.exception.messages;

import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.utils.enums.Status;

public final class AuthorizationExceptionMessages {

    public static String cantChangeStatus(Long userId, long taskId, Status currentStatus, Status newStatus) {
        String templateKey = "exception.unauthorized.task.status.change";
        String message = ExceptionMessages.getMessage(
            templateKey,
            User.class.getSimpleName(),
            userId,
            currentStatus,
            newStatus,
            Task.class.getSimpleName(),
            taskId);

        return message;
    }

    public static String neitherCreatorOrExecutor(Long taskId, Long userId) {
        String templateKey = "exception.unauthorized.task.neither.creator.nor.executor";
        String message = ExceptionMessages.getMessage(
            templateKey,
            User.class.getSimpleName(),
            userId,
            Task.class.getSimpleName(),
            taskId);
        return message;
    }
}
