package org.effective_mobile.task_management_system.database.repository;

import org.effective_mobile.task_management_system.database.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для сущности {@link Task}.
 */
@Repository
public interface TaskRepository extends AbstractJpaRepository<Task, Long> {
    Page<Task> findAll(Pageable pageable);
}
