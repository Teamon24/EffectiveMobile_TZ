package org.effective_mobile.task_management_system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MiscUtils {

    public static <O, V> V nullOrApply(O nullable, Function<O, V> mapper) {
        return nullable == null ? null : mapper.apply(nullable);
    }

    public static <O, V> List<V> emptyOrApply(List<O> nullable, Function<List<O>, List<V>> mapper) {
        return nullable == null ? List.of() : mapper.apply(nullable);
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

    public static StackTraceElement method() {
        return new Throwable().getStackTrace()[0];
    }

    public static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException(method().getMethodName());
    }
}
