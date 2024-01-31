package org.effective_mobile.task_management_system.component;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.database.repository.FilteredAndPagedTaskRepository;
import org.effective_mobile.task_management_system.database.repository.TaskRepository;
import org.effective_mobile.task_management_system.exception.AssignmentException;
import org.effective_mobile.task_management_system.exception.IllegalStatusChangeException;
import org.effective_mobile.task_management_system.exception.NothingToUpdateInTaskException;
import org.effective_mobile.task_management_system.exception.messages.TaskExceptionMessages;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TasksFiltersRequestPojo;
import org.effective_mobile.task_management_system.utils.MiscUtils;
import org.effective_mobile.task_management_system.utils.converter.TaskConverter;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.effective_mobile.task_management_system.maintain.cache.AppCacheNames.TASKS;

@Component
@CacheConfig(cacheNames = TASKS)
@AllArgsConstructor
public class TaskComponentImpl implements TaskComponent {

    private final TaskRepository taskRepository;
    private final FilteredAndPagedTaskRepository filteredAndPagedTaskRepository;

    @Cacheable(key = "#taskId")
    public Task getTask(Long taskId) {
        return taskRepository.findOrThrow(Task.class, taskId);
    }

    @CachePut(key = "#result.getId()")
    public Task createTask(User user, TaskCreationRequestPojo taskCreationPayload) {
        Task newTask = TaskConverter.convert(taskCreationPayload, user);
        Task save = taskRepository.save(newTask);
        return save;
    }

    @CachePut(key = "#result.getId()")
    public Task changeStatus(Task task, Status newStatus) {
        Status oldStatus = task.getStatus();
        if (Objects.equals(oldStatus, newStatus)) {
            String message = TaskExceptionMessages.sameStatusChange(task.getId(), newStatus);
            throw new IllegalStatusChangeException(message);
        }
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    @CachePut(key = "#result.getId()")
    public Task setExecutor(Task task, User user) {
        User oldExecutor = task.getExecutor();
        throwIfSameExecutor(task, user, oldExecutor);
        return setExecutorAndSave(task, user, Status.ASSIGNED);
    }

    @CachePut(key = "#result.getId()")
    public Task removeExecutor(Task task) {
        return setExecutorAndSave(task, null, Status.PENDING);
    }

    @CachePut(key = "#result.getId()")
    public Task editTask(Task task, TaskEditionRequestPojo payload) {
        String newPriority = payload.getPriority();
        String newContent = payload.getContent();

        boolean toSave = false;
        if (StringUtils.isNotBlank(newContent)) {
            toSave = setIfNew(task, newContent, Task::getContent, Task::setContent);
        }

        if (StringUtils.isNotBlank(newPriority)) {
            Priority priority = Priority.convert(newPriority);
            toSave = toSave | setIfNew(task, priority, Task::getPriority, Task::setPriority);
        }

        if (toSave) {
            return taskRepository.save(task);
        }

        String message = TaskExceptionMessages.nothingToChange(task.getId());
        throw new NothingToUpdateInTaskException(message);
    }

    @CacheEvict(key="#task.getId()")
    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    @Override
    public Page<Task> findByCreatorAndExecutor(TasksFiltersRequestPojo tasksFiltersPayload, Pageable pageable) {
        return filteredAndPagedTaskRepository
            .findByCreatorAndExecutor(
                tasksFiltersPayload.getCreatorUsername(),
                tasksFiltersPayload.getExecutorUsername(),
                pageable
            );
    }

    @Override
    @Deprecated(since = "it's for cache debug", forRemoval = true)
    public Collection<Task> getAll() {
        return taskRepository.findAll();
    }

    private <V> boolean setIfNew(
        Task task,
        V newValue,
        Function<Task, V> getter,
        BiConsumer<Task, V> setter
    ) {
        if (newValue != null) {
            boolean valueIsNew = !Objects.equals(newValue, getter.apply(task));
            if (valueIsNew) {
                setter.accept(task, newValue);
                return true;
            }
        }
        return false;
    }

    private Task setExecutorAndSave(Task task, User user, Status status) {
        task.setExecutor(user);
        task.setStatus(status);
        return taskRepository.save(task);
    }

    private void throwIfSameExecutor(
        Task task,
        User newExecutor,
        User oldExecutor
    ) {
        String oldUsername = MiscUtils.nullOrApply(oldExecutor, User::getUsername);
        String newExecutorUsername = newExecutor.getUsername();
        if (Objects.equals(oldUsername, newExecutorUsername)) {
            String message = TaskExceptionMessages.sameExecutorChange(task.getId(), newExecutorUsername);
            throw new AssignmentException(message);
        }
    }
}
