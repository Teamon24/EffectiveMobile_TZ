package org.effective_mobile.task_management_system.exception.messages;

import org.effective_mobile.task_management_system.database.entity.User;

public final class UserExceptionMessages {

    private static String simpleName;

    public static <ID> String notFound(ID id) {
        return EntityNotFoundMessages.notFound(User.class, id);
    }

    public static String notFound(String username) {
        simpleName = User.class.getSimpleName();
        return ExceptionMessages.getMessage(
            "exception.entity.notFound.user.by.username", simpleName, username);
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
