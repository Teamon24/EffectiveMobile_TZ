package org.effective_mobile.task_management_system.repository;

import org.effective_mobile.task_management_system.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Репозиторий для сущности {@link Task}.
 */
public interface TaskRepository extends AbstractJpaRepository<Task, Long> {
    Page<Task> findAll(Pageable pageable);

    Page<Task> findByExecutor_UsernameOrCreator_Username(
        String executorUsername,
        String creatorUsername,
        Pageable pageable
    );
}
