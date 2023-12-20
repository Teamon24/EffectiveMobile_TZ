package org.effective_mobile.task_management_system.converter;

import org.effective_mobile.task_management_system.entity.Comment;
import org.effective_mobile.task_management_system.pojo.task.CommentJsonPojo;

import java.util.List;
import java.util.stream.Collectors;

public class CommentConverter {

    public static CommentJsonPojo convert(Comment comment) {
        return new CommentJsonPojo(
            comment.getId(),
            comment.getContent(),
            comment.getCreationDate(),
            comment.getUser().getUsername()
        );
    }

    public static List<CommentJsonPojo> convert(List<Comment> comments) {
        return comments.stream().map(CommentConverter::convert).collect(Collectors.toList());
    }
}
