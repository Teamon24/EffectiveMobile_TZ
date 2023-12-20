package org.effective_mobile.task_management_system.service;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.database.entity.Comment;
import org.effective_mobile.task_management_system.database.repository.CommentRepository;
import org.effective_mobile.task_management_system.resource.json.CommentCreationRequestPojo;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class CommentService {

    private CommentRepository commentRepository;
    private TaskComponent taskComponent;
    private UserComponent userComponent;

    public Comment createComment(Long userId, Long taskId, CommentCreationRequestPojo requestPojo) {
        Comment comment = comment(userId, taskId, requestPojo);
        return commentRepository.save(comment);
    }

    private Comment comment(Long userId, Long taskId, CommentCreationRequestPojo requestPojo) {
        return Comment.builder()
            .creationDate(new Date(System.currentTimeMillis()))
            .content(requestPojo.getContent())
            .task(taskComponent.getTask(taskId))
            .user(userComponent.getById(userId))
            .build();
    }
}
