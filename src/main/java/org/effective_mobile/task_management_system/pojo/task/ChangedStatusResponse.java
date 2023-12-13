package org.effective_mobile.task_management_system.pojo.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.enums.Status;

@NoArgsConstructor
@AllArgsConstructor
public class ChangedStatusResponse {
    @Getter
    @JsonProperty
    private Long taskId;

    @Getter
    @JsonProperty
    private Status oldStatus;

    @Getter
    @JsonProperty
    private Status newStatus;
}
