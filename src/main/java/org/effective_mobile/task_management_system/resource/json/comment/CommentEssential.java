package org.effective_mobile.task_management_system.resource.json.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class CommentEssential {
    @NotNull
    @JsonProperty
    protected Long taskId;

    @NotBlank
    @JsonProperty
    @Length(min = 1, max = 256)
    protected String content;
}
