package org.effective_mobile.task_management_system.component;

import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TasksFiltersRequestPojo;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskComponent {
    Task getTask(Long taskId);
    Task createTask(User user, TaskCreationRequestPojo taskCreationPayload);
    Task changeStatus(Task task, Status newStatus);
    Task setExecutor(Task task, User user);
    Task removeExecutor(Task task);
    Task editTask(Task task, TaskEditionRequestPojo payload);
    void deleteTask(Long id);
    Page<Task> findByCreatorAndExecutor(TasksFiltersRequestPojo tasksFiltersPayload, Pageable pageable);
}
