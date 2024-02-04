package org.effective_mobile.task_management_system.resource;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.database.entity.Comment;
import org.effective_mobile.task_management_system.resource.json.comment.CommentCreationRequestPojo;
import org.effective_mobile.task_management_system.resource.json.comment.CommentCreationResponsePojo;
import org.effective_mobile.task_management_system.service.CommentService;
import org.effective_mobile.task_management_system.utils.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Api.COMMENT)
@AllArgsConstructor
public class CommentResource implements AuthenticatedResource {

    private CommentService commentService;

    @PostMapping
    public @ResponseBody CommentCreationResponsePojo createComment(
        @RequestBody @Valid CommentCreationRequestPojo commentCreationRequestPojo
    ) {
        Comment comment = commentService.createComment(commentCreationRequestPojo);
        Long id = comment.getId();
        return new CommentCreationResponsePojo(id, commentCreationRequestPojo);
    }
}
