package org.effective_mobile.task_management_system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MiscUtils {

    public static <E, V> V nullOrApply(E oldExecutor, Function<E, V> getter) {
        return oldExecutor == null ? null : getter.apply(oldExecutor);
    }

    public static <V> void evalIfNotNull(V value, Consumer<V> operation) {
        if (value != null) {
            operation.accept(value);
        }
    }

    /**
     * @param value nullable-значение.
     * @param operation логика, которая выполняется, если value - не null.
     * @return если value - null, то - null.
     *         если value - не null, то - результат выполнения operation.
     */
    public static <In, Out> Out evalIfNotNull(In value, Function<In, Out> operation) {
        if (value != null) {
            return operation.apply(value);
        }
        return null;
    }
}
