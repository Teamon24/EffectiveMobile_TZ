package org.effective_mobile.task_management_system.resource.json.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentCreationResponsePojo extends CommentEssential {
    @NotNull
    @JsonProperty
    private Long id;

    public CommentCreationResponsePojo(Long id, CommentCreationRequestPojo commentCreationRequestPojo) {
        this.id = id;
        super.content = commentCreationRequestPojo.content;
        super.taskId = commentCreationRequestPojo.taskId;
    }
}
