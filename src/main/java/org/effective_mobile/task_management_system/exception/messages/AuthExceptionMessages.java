package org.effective_mobile.task_management_system.exception.messages;

public final class AuthExceptionMessages {

    public static String noTokenInCookie(String authTokenName) {
        return ExceptionMessages.getMessage("exception.auth.token.absent", authTokenName);
    }
}
