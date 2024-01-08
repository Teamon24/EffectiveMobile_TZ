package org.effective_mobile.task_management_system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.effective_mobile.task_management_system.entity.AbstractEntity;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityAssertionUtils {

    public static <E extends AbstractEntity, T> void assertFieldsAreEqual(
        Pair<E, E> pair,
        Function<E, T> getter
    ) {
        assertEquals(getter.apply(pair.getLeft()), getter.apply(pair.getRight()));
    }

    public static <E extends AbstractEntity, T> void assertFieldsAreEqual(
        E e1, E e2,
        Function<E, T> getter
    ) {
        assertEquals(getter.apply(e1), getter.apply(e2));
    }

    public static <E extends AbstractEntity> void assertIdsAreEqual(Pair<E, E> pair) {
        assertFieldsAreEqual(pair, AbstractEntity::getId);
    }

    public static <E extends AbstractEntity> Stream<Pair<E, E>> pairsById(
        List<E> entities,
        List<E> others
    ) {
        return entities.stream()
            .map(role -> Pair.of(role, getById(others, role.getId())));
    }

    public static <E extends AbstractEntity> E getById(List<E> entities, Long id) {
        List<E> collect = entities.stream()
            .filter(it -> Objects.equals(id, it.getId()))
            .toList();

        assertEquals(collect.size(), 1);
        return collect.get(0);
    }
}
