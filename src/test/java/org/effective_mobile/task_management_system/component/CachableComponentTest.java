package org.effective_mobile.task_management_system.component;

import org.effective_mobile.task_management_system.database.entity.AbstractEntity;
import org.effective_mobile.task_management_system.database.repository.AbstractJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public abstract class CachableComponentTest<Id, E extends AbstractEntity, R extends AbstractJpaRepository<E, Id>> {

    private Boolean findOrThrowInteractionDisabled = false;
    private final Class<E> entityClass;

    protected R repository;

    protected CachableComponentTest(Class<E> entityClass, Class<R> repositoryClass) {
        this.repository = Mockito.mock(repositoryClass);
        this.entityClass = entityClass;
    }

    public abstract void beforeEach();
    public abstract void afterEach();

    @BeforeEach
    public void setUp() {
        beforeEach();
    }


    @BeforeEach
    public void resetFindInteractionFlag() {
        findOrThrowInteractionDisabled = false;
    }

    @AfterEach
    public void checkNoFindInteraction() {
        if (!findOrThrowInteractionDisabled) {
            Mockito.verify(repository, never()).findOrThrow(any(), any());
        } else {
            Mockito.verify(repository).findOrThrow(Mockito.eq(entityClass), any());
        }
        Mockito.verifyNoMoreInteractions(repository);
        afterEach();
    }

    public E verifyFindOrThrowInteraction(
        Id id,
        E task,
        Supplier<E> o
    ) {
        findOrThrowInteractionDisabled = true;
        Mockito.when(repository.findOrThrow(entityClass, id)).thenReturn(task);
        return o.get();
    }
}
