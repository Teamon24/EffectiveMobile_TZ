package org.effective_mobile.task_management_system.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.StatusChangeValidator;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.TasksFiltersRequestPojo;
import org.effective_mobile.task_management_system.security.ContextComponent;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.utils.converter.TaskConverter;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.effective_mobile.task_management_system.utils.MiscUtils.evalIfNotNull;

@Service
@Transactional
@AllArgsConstructor
public class TaskService {

    private ContextComponent contextComponent;
    private UserComponent userComponent;
    private TaskComponent taskComponent;
    private StatusChangeValidator statusChangeValidator;

    public Task createTask(TaskCreationRequestPojo taskCreationRequestPojo) {
        Long userId = contextComponent.getUserId();
        User creator = userComponent.getById(userId);
        Task task = taskComponent.createTask(creator, taskCreationRequestPojo);
        return task;
    }

    public Task getTask(@NotNull Long id) {
        Task task = taskComponent.getTask(id);
        return task;
    }

    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#id)")
    public void deleteTask(@NotNull Long id) {
        Task task = taskComponent.getTask(id);
        taskComponent.deleteTask(task);
    }

    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#taskId)")
    public User setExecutor(
        @NotNull Long taskId,
        @NotNull String newExecutorUsername
    ) {
        Task task = taskComponent.getTask(taskId);
        User newExecutor = userComponent.getByUsername(newExecutorUsername);
        User oldExecutor = task.getExecutor();
        taskComponent.setExecutor(task, newExecutor);
        return oldExecutor;
    }

    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#taskId)")
    public void removeExecutor(@NotNull Long taskId) {
        Task task = taskComponent.getTask(taskId);
        taskComponent.removeExecutor(task);
    }

    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#id)")
    public Task editTask(@NotNull Long id, TaskEditionRequestPojo requestPojo) {
        Task task = taskComponent.getTask(id);
        Task editedTask = taskComponent.editTask(task, requestPojo);
        return editedTask;
    }

    public Status setStatus(Long taskId, Status newStatus) {
        Task task = taskComponent.getTask(taskId);
        CustomUserDetails customUserDetails = contextComponent.getPrincipal();
        statusChangeValidator.validate(customUserDetails, task, newStatus);
        Status oldStatus = task.getStatus();
        taskComponent.changeStatus(task, newStatus);
        return oldStatus;
    }

    public Page<TaskResponsePojo> getByCreatorOrExecutor(
        TasksFiltersRequestPojo tasksFiltersRequestPojo,
        Pageable pageable
    ) {
        String creatorUsername = tasksFiltersRequestPojo.getCreatorUsername();
        String executorUsername = tasksFiltersRequestPojo.getExecutorUsername();

        evalIfNotNull(creatorUsername, (s) -> { userComponent.checkUsernameExists(s); });
        evalIfNotNull(executorUsername, (s) -> { userComponent.checkUsernameExists(s); });

        return taskComponent
            .findByCreatorAndExecutor(tasksFiltersRequestPojo, pageable)
            .map(task -> {
                Boolean withComments = tasksFiltersRequestPojo.getWithComments();
                return TaskConverter.convert(task, withComments);
            });
    }
}