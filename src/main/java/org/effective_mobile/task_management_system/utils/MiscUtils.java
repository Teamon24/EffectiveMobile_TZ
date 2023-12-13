package org.effective_mobile.task_management_system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.BiFunction;
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
}
