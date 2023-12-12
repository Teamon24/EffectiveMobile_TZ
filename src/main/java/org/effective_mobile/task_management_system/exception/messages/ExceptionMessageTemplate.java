package org.effective_mobile.task_management_system.exception.messages;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Хранит идентификатор и параметры сообщения, получаемого из {@link ExceptionMessages}.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public final class ExceptionMessageTemplate {
    @Getter
    private final @NonNull String key;
    @Getter
    private final Object[] params;

    /**
     * @param key    значение для {@link ExceptionMessageTemplate#key}.
     * @param params значение для {@link ExceptionMessageTemplate#params}.
     * @return новый экземпляр с заданным идентификатором.
     */
    public static ExceptionMessageTemplate get(@NonNull final String key, final Object... params) {
        return new ExceptionMessageTemplate(key, params);
    }

    public @NonNull String key() {
        return key;
    }

    public Object[] params() {
        return params;
    }

}
