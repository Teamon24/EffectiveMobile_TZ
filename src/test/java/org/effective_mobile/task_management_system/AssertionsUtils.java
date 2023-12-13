package org.effective_mobile.task_management_system;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssertionsUtils {
    public static <O, V> void assertEquals(O object, O other, Function<O, V> getter) {
        Assertions.assertEquals(getter.apply(object), getter.apply(other));
    }

    public static void assertCaselessEquals(String first, String second) {
        Assertions.assertEquals(first.toLowerCase(), second.toLowerCase());
    }

    public static <E extends Enum<E>> void assertEnumEquals(String first, E second) {
        Assertions.assertEquals(first.toLowerCase(), second.name().toLowerCase());
    }

    public static <E extends Enum<E>> void assertEnumEquals(E first, String second) {
        Assertions.assertEquals(first.name().toLowerCase(), second.toLowerCase());
    }
}
