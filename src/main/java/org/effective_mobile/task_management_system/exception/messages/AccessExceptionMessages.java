package org.effective_mobile.task_management_system.exception.messages;

import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;

import static org.effective_mobile.task_management_system.exception.messages.ExceptionMessages.getMessage;

public class AccessExceptionMessages {
    public static String notACreator(Long taskId, String usernameAtDb) {
        return getMessage(
            "exception.access.task.notCreator",
            User.class.getSimpleName(),
            usernameAtDb,
            Task.class.getSimpleName(),
            taskId
        );
    }

    public static String notAExecutor(Long taskId, String usernameAtDb) {
        return getMessage(
            "exception.access.task.notExecutor",
            User.class.getSimpleName(),
            usernameAtDb,
            Task.class.getSimpleName(),
            taskId
        );
    }
}
