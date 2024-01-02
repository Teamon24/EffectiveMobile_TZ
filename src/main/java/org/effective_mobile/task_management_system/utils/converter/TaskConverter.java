package org.effective_mobile.task_management_system.utils.converter;

import org.effective_mobile.task_management_system.database.entity.Comment;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.comment.CommentJsonPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreator;
import org.effective_mobile.task_management_system.resource.json.task.TaskExecutor;
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo;
import org.effective_mobile.task_management_system.utils.MiscUtils;
import org.effective_mobile.task_management_system.utils.enums.Priority;

import java.util.List;

public class TaskConverter {

    public static TaskResponsePojo convertNew(Task newTask) {
        return convert(newTask, false);
    }

    public static TaskResponsePojo convertEdited(Task newTask) {
        return convert(newTask, false);
    }

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
            executorInfo(executor),
            creatorInfo(creator),
            convertComments(task, withComments)
        );
    }

    private static TaskExecutor executorInfo(User executor) {
        return MiscUtils.nullOrApply(executor, TaskConverter::createExecutorInfo);
    }

    private static TaskExecutor createExecutorInfo(User executor) {
        Long executorId = executor.getId();
        String executorUsername = executor.getUsername();
        return new TaskExecutor(executorId, executorUsername);
    }

    private static TaskCreator creatorInfo(User creator) {
        return new TaskCreator(creator.getId(), creator.getUsername());
    }

    private static List<CommentJsonPojo> convertComments(Task task, Boolean withComments) {
        List<Comment> comments = withComments ? task.getComments() : List.of();
        return MiscUtils.emptyOrApply(comments, CommentConverter::convert);
    }
}
