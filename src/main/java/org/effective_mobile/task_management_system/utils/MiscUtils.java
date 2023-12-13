package org.effective_mobile.task_management_system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MiscUtils {
    public static <E, V> V nullOrApply(E oldExecutor, Function<E, V> getter) {
        return oldExecutor == null ? null : getter.apply(oldExecutor);
    }
}
