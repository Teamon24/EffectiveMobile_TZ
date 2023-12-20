package org.effective_mobile.task_management_system.database.repository;

import io.micrometer.common.lang.Nullable;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Репозиторий для сущности {@link Task}, который поддерживает фильтрацию и пагинацию
 */
public interface FilteredAndPagedTaskRepository {
    /**
     * @param creator username создателя задачи.
     * @param executor username исполнителя задачи.
     * @param pageable объект с информацией о пагинации.
     * @return объект, содержащий результат поиска и информацию о странице пагинации.
     */
    Page<Task> findByCreatorAndExecutor(@Nullable String creator, @Nullable String executor, Pageable pageable);
}
