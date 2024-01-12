package org.effective_mobile.task_management_system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.effective_mobile.task_management_system.entity.HasId;
import org.effective_mobile.task_management_system.entity.HasLongId;

import java.text.Format;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityAssertionUtils {

    public static <Id, E extends HasId<Id>> void assertDatesAreEqual(
        E e,
        E another,
        Format format,
        Function<E, Date> getter
    ) {
        Date date = getter.apply(e);
        Date anotherDate = getter.apply(another);
        if (date == null && anotherDate == null) return;
        assertEquals(format.format(date), format.format(anotherDate));
    }

    public static <Id, F, E extends HasId<Id>> void assertFieldsAreEqual(
        Pair<E, E> pair,
        Function<E, F> getter
    ) {
        assertEquals(getter.apply(pair.getLeft()), getter.apply(pair.getRight()));
    }

    public static <Id, F, E extends HasId<Id>> void assertFieldsAreEqual(
        E e1, E e2,
        Function<E, F> getter
    ) {
        assertEquals(getter.apply(e1), getter.apply(e2));
    }

    public static <E extends HasLongId> void assertIdsAreEqual(Pair<E, E> pair) {
        assertFieldsAreEqual(pair, HasLongId::getId);
    }

    public static <E extends HasLongId> Stream<Pair<E, E>> pairsStreamById(
        List<E> entities,
        List<E> others
    ) {
        return entities.stream()
            .map(role -> Pair.of(role, getById(others, role.getId())));
    }

    public static <E extends HasLongId> List<Pair<E, E>> pairsById(
        List<E> entities,
        List<E> others
    ) {
        return entities.stream()
            .map(role -> Pair.of(role, getById(others, role.getId()))).toList();
    }

    public static <E extends HasLongId> E getById(List<E> entities, Long id) {
        List<E> collect = entities.stream()
            .filter(it -> Objects.equals(id, it.getId()))
            .toList();

        assertEquals(collect.size(), 1);
        return collect.get(0);
    }
}
