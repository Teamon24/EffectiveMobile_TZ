package org.effective_mobile.task_management_system;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssertionsUtils {
    public static <O, V> void assertEquals(O expected, O actual, Function<O, V> getter) {
        Assertions.assertEquals(getter.apply(expected), getter.apply(actual));
    }

    public static void assertCaselessEquals(String expected, String actual) {
        Assertions.assertEquals(expected.toLowerCase(), actual.toLowerCase());
    }

    public static <E extends Enum<E>> void assertEnumEquals(String expected, E actual) {
        Assertions.assertEquals(expected.toLowerCase(), actual.name().toLowerCase());
    }

    public static <E extends Enum<E>> void assertEnumEquals(E expected, String actual) {
        Assertions.assertEquals(expected.name().toLowerCase(), actual.toLowerCase());
    }
}
