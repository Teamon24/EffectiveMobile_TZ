package org.effective_mobile.task_management_system.resource.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
public class CommentCreationRequestPojo {
    @NotBlank
    @JsonProperty
    @Getter
    @Length(min = 1, max = 256)
    private String content;
}
