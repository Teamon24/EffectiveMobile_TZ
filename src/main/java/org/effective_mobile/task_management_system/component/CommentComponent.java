package org.effective_mobile.task_management_system.component;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.database.entity.Comment;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.database.repository.CommentRepository;
import org.effective_mobile.task_management_system.resource.json.comment.CommentCreationRequestPojo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.effective_mobile.task_management_system.maintain.cache.AppCacheNames.TASKS;

@Component
@AllArgsConstructor
public class CommentComponent {

    private CommentRepository commentRepository;

    @CacheEvict(cacheNames = TASKS, key = "#task.getId()")
    public Comment createComment(User user, Task task, CommentCreationRequestPojo requestPojo) {
        Comment comment = Comment.builder()
            .creationDate(new Date(System.currentTimeMillis()))
            .content(requestPojo.getContent())
            .task(task)
            .user(user)
            .build();

        return commentRepository.save(comment);
    }
}
