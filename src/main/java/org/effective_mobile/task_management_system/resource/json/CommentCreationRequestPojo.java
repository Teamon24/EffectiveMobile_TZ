package org.effective_mobile.task_management_system.resource.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class CommentCreationRequestPojo {
    @NotNull
    @JsonProperty
    private Long taskId;

    @NotBlank
    @JsonProperty
    @Length(min = 1, max = 256)
    private String content;
}
