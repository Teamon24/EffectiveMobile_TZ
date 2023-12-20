package org.effective_mobile.task_management_system.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.converter.TaskConverter;
import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.enums.Status;
import org.effective_mobile.task_management_system.pojo.TasksFiltersRequestPojo;
import org.effective_mobile.task_management_system.pojo.assignment.AssignmentResponse;
import org.effective_mobile.task_management_system.pojo.task.ChangedStatusResponsePojo;
import org.effective_mobile.task_management_system.pojo.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.pojo.task.TaskEditionRequestPojo;
import org.effective_mobile.task_management_system.pojo.task.TaskResponsePojo;
import org.effective_mobile.task_management_system.utils.MiscUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.effective_mobile.task_management_system.utils.MiscUtils.evalIfNotNull;

@Service
@AllArgsConstructor
public class TaskService {

    private UserComponent userComponent;
    private TaskComponent taskComponent;

    @Transactional
    public TaskResponsePojo createTask(Long userId, TaskCreationRequestPojo taskCreationRequestPojo) {
        User creator = userComponent.getById(userId);
        Task task = taskComponent.createTask(creator, taskCreationRequestPojo);
        return TaskConverter.convert(task, false);
    }

    public TaskResponsePojo getTask(Long id) {
        return taskComponent.getJsonPojo(id);
    }

    @Transactional
    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#id)")
    public void deleteTask(@NotNull Long id) {
        taskComponent.deleteTask(id);
    }

    @Transactional
    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#taskId)")
    public AssignmentResponse setExecutor(
        @NotNull Long taskId,
        @NotNull String executorUsername
    ) {
        User user = userComponent.getByUsername(executorUsername);
        User oldExecutor = taskComponent.getTask(taskId).getExecutor();
        taskComponent.setExecutor(taskId, user);
        String oldExecutorUsername = MiscUtils.nullOrApply(oldExecutor, User::getUsername);
        return new AssignmentResponse(taskId, user.getUsername(), oldExecutorUsername);
    }

    @Transactional
    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#taskId)")
    public Long unassign(@NotNull Long taskId) {
        return taskComponent.removeExecutor(taskId).getId();
    }

    @Transactional
    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#id)")
    public TaskResponsePojo editTask(@NotNull Long id, TaskEditionRequestPojo requestPojo) {
        Task task = taskComponent.editTask(id, requestPojo);
        return TaskConverter.convert(task, false);
    }

    @Transactional
    public ChangedStatusResponsePojo setStatus(Long taskId, Status newStatus) {
        taskComponent.validateStatusChange(taskId, newStatus);
        Status oldStatus = taskComponent.getStatus(taskId);
        taskComponent.changeStatus(taskId, newStatus);
        return new ChangedStatusResponsePojo(taskId, oldStatus, newStatus);
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