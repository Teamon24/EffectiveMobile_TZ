package org.effective_mobile.task_management_system.utils.converter;

import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.CommentJsonPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreator;
import org.effective_mobile.task_management_system.resource.json.task.TaskExecutor;
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo;
import org.effective_mobile.task_management_system.utils.enums.Priority;

import java.util.List;
import java.util.Objects;

public class TaskConverter {

    public static Task convert(TaskCreationRequestPojo taskCreationPayload, User user) {
        return Task.builder()
            .content(taskCreationPayload.getContent())
            .creator(user)
            .priority(Priority.valueOf(taskCreationPayload.getPriority()))
            .build();
    }

    public static TaskResponsePojo convert(Task task, Boolean withComments) {
        User executor = task.getExecutor();
        User creator = task.getCreator();
        return new TaskResponsePojo(
            task.getId(),
            task.getContent(),
            task.getStatus().name(),
            task.getPriority().name(),
            executorInfoOrNull(executor),
            new TaskCreator(creator.getId(), creator.getUsername()),
            convertComments(task, withComments)
        );
    }

    private static TaskExecutor executorInfoOrNull(User executor) {
        return Objects.isNull(executor) ?
            null : new TaskExecutor(executor.getId(), executor.getUsername());
    }

    private static List<CommentJsonPojo> convertComments(Task task, Boolean withComments) {
        return withComments ? CommentConverter.convert(task.getComments()) : List.of();
    }
}
