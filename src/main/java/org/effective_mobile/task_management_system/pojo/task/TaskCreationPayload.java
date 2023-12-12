package org.effective_mobile.task_management_system.pojo.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.enums.Priority;
import org.effective_mobile.task_management_system.enums.Status;

/**
 * Json pojo с информацией для создания задачи.
 */
@Getter
@Setter
@NoArgsConstructor
public class TaskCreationPayload extends TaskEssential {
    @NotNull
    @JsonProperty
    private String creatorUsername;

    public TaskCreationPayload(
        String status,
        String priority,
        String content,
        String creatorUsername
    ) {
        super(status, priority, content);
        this.creatorUsername = creatorUsername;
    }
}

