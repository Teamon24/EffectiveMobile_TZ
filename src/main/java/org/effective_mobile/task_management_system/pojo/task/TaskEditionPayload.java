package org.effective_mobile.task_management_system.pojo.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.enums.Priority;
import org.effective_mobile.task_management_system.validator.ValidEnum;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskEditionPayload {

    @JsonProperty
    private String newContent;

    @JsonProperty
    private String newPriority;
}
