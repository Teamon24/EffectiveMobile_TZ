package org.effective_mobile.task_management_system.converter;

import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.enums.Priority;
import org.effective_mobile.task_management_system.pojo.CommentJsonPojo;
import org.effective_mobile.task_management_system.pojo.task.TaskCreationPayload;
import org.effective_mobile.task_management_system.pojo.task.TaskCreatorJsonPojo;
import org.effective_mobile.task_management_system.pojo.task.TaskExecutorJsonPojo;
import org.effective_mobile.task_management_system.pojo.task.TaskJsonPojo;

import java.util.List;
import java.util.Objects;

public class TaskConverter {
    public static Task convert(TaskCreationPayload taskCreationPayload, User user) {
        return Task.builder()
            .content(taskCreationPayload.getContent())
            .creator(user)
            .priority(Priority.valueOf(taskCreationPayload.getPriority()))
            .build();
    }

    public static TaskJsonPojo convert(Task task, Boolean withComments) {
        User executor = task.getExecutor();
        User creator = task.getCreator();
        return new TaskJsonPojo(
            task.getId(),
            task.getContent(),
            task.getStatus().name(),
            task.getPriority().name(),
            executorInfoOrNull(executor),
            new TaskCreatorJsonPojo(creator.getId(), creator.getUsername()),
            convertComments(task, withComments)
        );
    }

    private static TaskExecutorJsonPojo executorInfoOrNull(User executor) {
        return Objects.isNull(executor) ?
            null : new TaskExecutorJsonPojo(executor.getId(), executor.getUsername());
    }

    private static List<CommentJsonPojo> convertComments(Task task, Boolean withComments) {
        return withComments ? CommentConverter.convert(task.getComments()) : List.of();
    }
}
