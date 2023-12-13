package org.effective_mobile.task_management_system.service;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.entity.Comment;
import org.effective_mobile.task_management_system.pojo.CommentCreationPayload;
import org.effective_mobile.task_management_system.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class CommentService {

    private CommentRepository commentRepository;
    private TaskComponent taskComponent;
    private UserComponent userComponent;

    public Comment createComment(Long userId, Long taskId, CommentCreationPayload commentCreationPayload) {
        Comment comment = comment(userId, taskId, commentCreationPayload);
        return commentRepository.save(comment);
    }

    private Comment comment(Long userId, Long taskId, CommentCreationPayload commentCreationPayload) {
        return Comment.builder()
            .creationDate(new Date(System.currentTimeMillis()))
            .content(commentCreationPayload.getContent())
            .task(taskComponent.getTask(taskId))
            .user(userComponent.getById(userId))
            .build();
    }
}
