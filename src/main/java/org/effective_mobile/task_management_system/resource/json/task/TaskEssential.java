package org.effective_mobile.task_management_system.resource.json.task;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.effective_mobile.task_management_system.component.validator.ValidEnum;

/**
 * Json pojo с информацией для создания задачи.
 */
@RequiredArgsConstructor
@Getter
@Setter
public abstract class TaskEssential {

    @ValidEnum(clazz = Priority.class)
    @JsonProperty
    private String priority;

    @NotEmpty
    @JsonProperty
    private String content;

    public TaskEssential(
        String priority,
        String content
    ) {
        this.priority = priority;
        this.content = content;
    }
}

