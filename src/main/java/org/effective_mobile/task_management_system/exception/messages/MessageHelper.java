package org.effective_mobile.task_management_system.exception.messages;

import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Класс, содержащий основную логику обращения к *.properties-файлам.
 */
final class MessageHelper {

    /**
     * Сервис, обеспечивающий доступ к properties файлу, для получение сообщений. */
    private final ResourceBundle resource;

    /**
     * @param propertiesFilename название properties файла с сообщениями.
     */
    MessageHelper(final String propertiesFilename) {
        this.resource = ResourceBundle.getBundle(propertiesFilename, Locale.getDefault());
    }

    /**
     * Достаёт сообщение для исключения, по ключу,
     * и формирует текст сообщения, используя параметры.
     * @param templateKey ключ к шаблон-сообщению в properties файле.
     * @param params параметры сообщения.
     * @return сообщение, соответствующее переданному ключу с заданными параметрами.
     */
    public String getMessage(@NonNull final String templateKey, final Object... params) {
        final String message = getMessage(templateKey);
        return params.length > 0 ? String.format(message, params) : message;
    }

    /**
     * Достаёт сообщение для исключения, по ключу.
     * @param templateKey ключ для получения доступа к сообщению в properties файле.
     * @return сообщение, соответствующее переданному ключу.
     */
    private String getMessage(final String templateKey) {
        final String message = resource.getString(templateKey);
        return new String(message.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
