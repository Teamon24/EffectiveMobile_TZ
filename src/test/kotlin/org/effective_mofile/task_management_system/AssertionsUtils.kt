package org.effective_mofile.task_management_system

import lombok.NoArgsConstructor
import lombok.AccessLevel
import org.junit.jupiter.api.Assertions
import java.util.Locale
import java.util.function.Consumer
import java.util.function.Function

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object AssertionsUtils {
    @JvmStatic
    fun <O, V> assertEquals(expected: O, actual: O, getter: Function<O, V>) {
        Assertions.assertEquals(getter.apply(expected), getter.apply(actual))
    }

    @JvmStatic
    fun <O, V> assertEquals(expected: O, actual: O, getters: List<Function<O, V>>) {
        getters.forEach(Consumer { getter: Function<O, V> -> assertEquals(expected, actual, getter) })
    }

    @JvmStatic
    fun assertCaselessEquals(expected: String?, actual: String?) {
        Assertions.assertEquals(expected?.lowercase(), actual?.lowercase())
    }

    @JvmStatic
    fun <E : Enum<E>> assertEnumEquals(expected: String?, actual: E?) {
        assertCaselessEquals(expected, actual?.name)
    }

    @JvmStatic
    fun <E : Enum<E>> assertEnumEquals(expected: E?, actual: String?) {
        Assertions.assertEquals(expected?.name?.lowercase(), actual?.lowercase())
    }
}