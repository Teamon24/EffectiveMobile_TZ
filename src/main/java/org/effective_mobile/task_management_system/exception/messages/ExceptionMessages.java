package org.effective_mobile.task_management_system.exception.messages;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Утилита, позволяющая получить сообщения об ошибках.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionMessages {
    /**
     * Название properties файла с сообщениями исключений. */
    public static final String EXCEPTION_PROPERTIES_FILE_NAME = "messages";

    /**
     * Сервис, обеспечивающий доступ к properties файлу, для получение сообщений. */
    private static final MessageHelper MESSAGE_HELPER = new MessageHelper(EXCEPTION_PROPERTIES_FILE_NAME);

    /**
     * Получает сообщение для исключения.
     * @param templateKey dto с ключом и параметрами, для получения доступа к шаблон-сообщению в properties файле.
     * @return сообщение, соответствующее переданному ключу с параметрами.
     */
    public static String getMessage(@NonNull final ExceptionMessageTemplate templateKey) {
        return ExceptionMessages.getMessage(templateKey.key(), templateKey.params());
    }

    /**
     * Получает сообщение для исключения, по ключу,
     * и формирует текст сообщения, используя параметры.
     * @param templateKey ключ к шаблон-сообщению в properties файле.
     * @param params параметры сообщения.
     * @return сообщение, соответствующее переданному ключу с заданными параметрами.
     */
    public static String getMessage(@NonNull final String templateKey, final Object... params) {
        return MESSAGE_HELPER.getMessage(templateKey, params);
    }
}
