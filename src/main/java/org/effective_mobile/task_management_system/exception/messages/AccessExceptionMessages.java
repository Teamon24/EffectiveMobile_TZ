package org.effective_mobile.task_management_system.exception.messages;

import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.security.CustomUserDetails;

import static org.effective_mobile.task_management_system.exception.messages.ExceptionMessages.getMessage;

public final class AccessExceptionMessages {
    public static String notACreator(CustomUserDetails customUserDetails, Long taskId) {
        return getMessage(
            "exception.unauthorized.task.notCreator",
            User.class.getSimpleName(),
            customUserDetails.getUserId(),
            Task.class.getSimpleName(),
            taskId
        );
    }

    public static String notAExecutor(CustomUserDetails customUserDetails, Long taskId) {
        return getMessage(
            "exception.unauthorized.task.notExecutor",
            User.class.getSimpleName(),
            customUserDetails.getUserId(),
            Task.class.getSimpleName(),
            taskId
        );
    }
}
