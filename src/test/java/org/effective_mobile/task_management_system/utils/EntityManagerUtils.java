package org.effective_mobile.task_management_system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.entity.AbstractEntity;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityManagerUtils {

    public static <E> void persist(TestEntityManager testEntityManager, E... entities) {
        Arrays.stream(entities).forEach(testEntityManager::persist);
    }

    public static <E> void 
    persist(TestEntityManager testEntityManager, Collection<E> entities) {
        entities.forEach(testEntityManager::persist);
    }

    public static <E> void refresh(TestEntityManager testEntityManager, E... entities) {
        Arrays.stream(entities).forEach(testEntityManager::refresh);
    }

    public static <E extends AbstractEntity> void persistFlushRefresh(TestEntityManager testEntityManager, E... entities) {
        persist(testEntityManager, entities);
        testEntityManager.flush();
        refresh(testEntityManager, entities);
    }

    public static <E extends AbstractEntity> void persistFlushRefresh(TestEntityManager testEntityManager, Collection<E> entities) {
        persist(testEntityManager, entities);
        testEntityManager.flush();
        refresh(testEntityManager, entities);
    }
}
