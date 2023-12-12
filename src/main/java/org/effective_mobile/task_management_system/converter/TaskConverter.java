package org.effective_mobile.task_management_system.converter;

import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.enums.Priority;
import org.effective_mobile.task_management_system.enums.Status;
import org.effective_mobile.task_management_system.pojo.task.TaskCreationPayload;
import org.effective_mobile.task_management_system.pojo.task.TaskCreatorJsonPojo;
import org.effective_mobile.task_management_system.pojo.task.TaskExecutorJsonPojo;
import org.effective_mobile.task_management_system.pojo.task.TaskJsonPojo;

import java.util.Objects;

public class TaskConverter {
    public static Task convert(TaskCreationPayload taskCreationPayload, User user) {
        return Task.builder()
            .content(taskCreationPayload.getContent())
            .creator(user)
            .status(Status.valueOf(taskCreationPayload.getStatus()))
            .priority(Priority.valueOf(taskCreationPayload.getPriority()))
            .build();
    }

    public static TaskJsonPojo convert(Task task) {
        User executor = task.getExecutor();
        User creator = task.getCreator();
        return new TaskJsonPojo(
            task.getId(),
            task.getContent(),
            task.getStatus().name(),
            task.getPriority().name(),
            Objects.isNull(executor) ? null : new TaskExecutorJsonPojo(executor.getId(), executor.getUsername()),
            new TaskCreatorJsonPojo(creator.getId(), creator.getUsername()),
            CommentConverter.convert(task.getComments()));
    }
}
