package org.effective_mobile.task_management_system.service;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.CommentComponent;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.database.entity.Comment;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.CommentCreationRequestPojo;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {

    private CommentComponent commentComponent;
    private TaskComponent taskComponent;
    private UserComponent userComponent;

    public Comment createComment(
        Long userId,
        Long taskId,
        CommentCreationRequestPojo commentCreationRequestPojo
    ) {
        User user = userComponent.getById(userId);
        Task task = taskComponent.getTask(taskId);
        return this.commentComponent.createComment(user, task, commentCreationRequestPojo);
    }
}
