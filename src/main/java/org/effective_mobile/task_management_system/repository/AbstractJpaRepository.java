package org.effective_mobile.task_management_system.repository;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.effective_mobile.task_management_system.entity.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.effective_mobile.task_management_system.exception.messages.ExceptionMessages.getMessage;

public interface AbstractJpaRepository<T extends AbstractEntity, ID>
    extends
    JpaRepository<T, ID>
{

    /**
     * Вызывает {@link CrudRepository#findById}, и возвращает результат, если сущность найдена,
     * иначе выбрасывает заданное исключение с заданным сообщением.
     * @param id идентификатор сущности.
     * @return найденная сущность, не возвращает null.
     */
    default T findOrThrow(Class<T> entityClass, final ID id) {
        return findById(id).orElseThrow(() -> {
            String message = getMessage("exception.entity.notFound.id", entityClass.getSimpleName(), id);
            return createException(EntityNotFoundException.class, message);
        });
    }

    /**
     * Создает заданное исключение с заданным сообщением.
     * У создаваемого исключения должен быть конструктор вида
     * {@link RuntimeException#RuntimeException(java.lang.String)}.
     * @param exceptionClass класс создаваемого исключения.
     * @param message сообщение создаваемого исключения.
     * @param <Exception> тип создаваемого исключения.
     * @return созданное исключение.
     */
    static <Exception extends Throwable> Exception createException(
        @NonNull final Class<Exception> exceptionClass,
        @NonNull final String message
    )
    {
        try {
            final Constructor<Exception> constructor = exceptionClass.getDeclaredConstructor(String.class);
            final Exception exception = constructor.newInstance(message);
            return exception;
        } catch (NoSuchMethodException
            | IllegalAccessException
            | InstantiationException
            | InvocationTargetException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
