package org.effective_mobile.task_management_system.exception.messages;

import lombok.val;
import org.effective_mobile.task_management_system.database.entity.User;

public final class UserExceptionMessages {

    public enum NotFoundBy {
        USERNAME, EMAIL
    }

    private static String simpleName;

    public static <ID> String notFound(ID id) {
        return EntityNotFoundMessages.notFound(User.class, id);
    }

    public static String notFound(String username) {
        return notFoundBy(NotFoundBy.USERNAME, username);
    }

    public static String notFoundBy(NotFoundBy fieldType, String value) {
        simpleName = User.class.getSimpleName();
        val templateKey = switch (fieldType) {
            case USERNAME -> "exception.entity.notFound.user.by.username";
            case EMAIL -> "exception.entity.notFound.user.by.email";
        };
        return ExceptionMessages.getMessage(templateKey, simpleName, value);
    }

    public static String usernameExists(String username) {
        return ExceptionMessages.getMessage(
            "exception.signup.usernameExists", simpleName, username
        );
    }

    public static String emailExists(String email) {
        return ExceptionMessages.getMessage(
            "exception.signup.emailExists", simpleName, email);
    }
}
