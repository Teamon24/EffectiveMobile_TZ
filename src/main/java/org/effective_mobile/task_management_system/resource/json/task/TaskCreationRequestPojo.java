package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.component.validator.ValidEnum;
import org.effective_mobile.task_management_system.resource.json.RequestPojo;
import org.effective_mobile.task_management_system.utils.enums.Priority;

/**
 * Json pojo с информацией для создания задачи.
 */
@Getter
@Setter
@NoArgsConstructor
public class TaskCreationRequestPojo implements RequestPojo {

    @ValidEnum(clazz = Priority.class)
    @JsonProperty
    private String priority;

    @NotBlank
    @JsonProperty
    private String content;

    @Builder
    private TaskCreationRequestPojo(String priority, String content) {
        this.priority = priority;
        this.content = content;
    }
}

