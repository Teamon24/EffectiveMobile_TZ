package org.effective_mobile.task_management_system.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.converter.TaskConverter;
import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.enums.Status;
import org.effective_mobile.task_management_system.pojo.assignment.AssignmentResponse;
import org.effective_mobile.task_management_system.pojo.task.ChangedStatusResponse;
import org.effective_mobile.task_management_system.pojo.task.TaskCreationPayload;
import org.effective_mobile.task_management_system.pojo.task.TaskJsonPojo;
import org.effective_mobile.task_management_system.pojo.task.TaskEditionPayload;
import org.effective_mobile.task_management_system.repository.TaskRepository;
import org.effective_mobile.task_management_system.security.AuthorizationComponent;
import org.effective_mobile.task_management_system.utils.MiscUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TaskService {

    private UserComponent userComponent;
    private TaskRepository taskRepository;
    private TaskComponent taskComponent;
    private AuthorizationComponent authorizationComponent;

    @Transactional
    public Long createTask(Long userId, TaskCreationPayload taskCreationPayload) {
        User creator = userComponent.getById(userId);
        Task task = taskComponent.createTask(creator, taskCreationPayload);
        return task.getId();
    }

    public TaskJsonPojo getTask(Long id) {
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
    public ChangedStatusResponse setStatus(Long taskId, Status newStatus) {
        authorizationComponent.canChangeStatus(taskId, newStatus);
        Status oldStatus = taskComponent.getStatus(taskId);
        taskComponent.changeStatus(taskId, newStatus);
        return new ChangedStatusResponse(taskId, oldStatus, newStatus);
    }

    @Transactional
    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#id)")
    public TaskJsonPojo editTask(@NotNull Long id, TaskEditionPayload payload) {
        Task task = taskComponent.editTask(id, payload);
        return TaskConverter.convert(task);
    }

    public Page<TaskJsonPojo> getByCreatorOrExecutor(
        String creatorUsername,
        String executorUsername,
        Pageable page
    ) {
        Page<Task> tasks = taskRepository.findAll(page);
        return tasks.map(TaskConverter::convert);
    }
}
